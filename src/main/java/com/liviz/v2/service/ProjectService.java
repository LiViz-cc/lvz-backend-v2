package com.liviz.v2.service;

import com.liviz.v2.dao.DataSourceDao;
import com.liviz.v2.dao.DisplaySchemaDao;
import com.liviz.v2.dao.ProjectDao;
import com.liviz.v2.exception.BadRequestException;
import com.liviz.v2.exception.NoSuchElementFoundException;
import com.liviz.v2.model.DataSource;
import com.liviz.v2.model.DisplaySchema;
import com.liviz.v2.model.Project;
import com.liviz.v2.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {
    @Autowired
    ProjectDao projectDao;

    @Autowired
    DisplaySchemaDao displaySchemaDao;

    @Autowired
    DataSourceDao dataSourceDao;

    public Optional<Project> findByIdAndUserId(String id, String userId) {
        return projectDao.findByIdAndUserId(id, userId);
    }

    public void deleteByIdAndUserId(String id, String userId) {
        // find project by id and user id
        Optional<Project> projectOptional = projectDao.findByIdAndUserId(id, userId);

        // delete project if found
        if (projectOptional.isPresent()) {
            projectDao.delete(projectOptional.get());
        } else {
            // throw exception if not found
            throw new NoSuchElementFoundException(String.format("Project not found with id %s and current user", id));
        }
    }

    public List<Project> getProjectsByFilters(User user, Boolean isPublic, String createdById) {
        // if requested user is not the jwt user
        if (user == null || !user.getId().equals(createdById)) {
            // if isPublic is not True
            if (isPublic == null || !isPublic) {
                throw new BadRequestException("This query combination is not allowed.");
            }
        }

        if (isPublic != null && createdById != null) {
            return projectDao.queryByFilters(isPublic, createdById);
        } else if (isPublic != null) {
            return projectDao.queryByIsPublic(isPublic);
        } else if (createdById != null) {
            return projectDao.queryByCreatedBy(createdById);
        }

        throw new BadRequestException("This query combination is not allowed.");

    }

    public Project cloneProject(String projectId, User user) {
        // find project by id
        Optional<Project> projectOptional = projectDao.findById(projectId);

        // if project is not found
        if (projectOptional.isEmpty()) {
            throw new NoSuchElementFoundException(String.format("Project not found with id %s and current user", projectId));
        }

        Project project = projectOptional.get();

        // check if project is public or created by user
        if (project.getIsPublic() != Boolean.TRUE && !project.getCreatedBy().getId().equals(user.getId())) {
            throw new BadRequestException("This project is not public and not created by current user.");
        }

        // create new project
        Project newProject = new Project(project);

        // set new project's id to null
        newProject.setId(null);

        // set new project's created by to current user
        newProject.setCreatedBy(user);

        // save and return new project
        return projectDao.save(newProject);
    }

    public Optional<Project> addProjectDisplaySchema(String id, @NotNull User user, String displaySchemaId) {
        // find project by id and user id
        Optional<Project> projectOptional = projectDao.findByIdAndUserId(id, user.getId());

        // if project is not found
        if (projectOptional.isEmpty()) {
            return projectOptional;
        }

        Project project = projectOptional.get();

        // get display schema
        Optional<DisplaySchema> displaySchemaOptional = displaySchemaDao.findByIdAndUserId(displaySchemaId, user.getId());

        // if display schema is not found
        if (displaySchemaOptional.isEmpty()) {
            throw new NoSuchElementFoundException(String.format("Display schema not found with id %s", displaySchemaId));
        }

        // add display schema id to project and vice versa
        if (project.getDisplaySchema() != null) {
            project.getDisplaySchema().setLinkedProject(null);
            displaySchemaDao.save(project.getDisplaySchema());
        }
        displaySchemaOptional.get().setLinkedProject(project);
        displaySchemaDao.save(displaySchemaOptional.get());
        project.setDisplaySchema(displaySchemaOptional.get());

        // save and return project
        return Optional.of(projectDao.save(project));
    }


    public Optional<Project> deleteProjectDisplaySchema(String projectId, @NotNull User user) {
        // find project by id and user id
        Optional<Project> projectOptional = projectDao.findByIdAndUserId(projectId, user.getId());

        // if project is not found
        if (projectOptional.isEmpty()) {
            return projectOptional;
        }

        Project project = projectOptional.get();

        // remove display schema from project and vice versa
        if (project.getDisplaySchema() != null) {
            project.getDisplaySchema().setLinkedProject(null);
            displaySchemaDao.save(project.getDisplaySchema());
        }
        project.setDisplaySchema(null);

        // save and return project
        return Optional.of(projectDao.save(project));
    }

    public @NotNull Project addProjectDataSource(String projectId, User user, List<String> dataSourceIds) {
        // find project by id and user id
        Optional<Project> projectOptional = projectDao.findByIdAndUserId(projectId, user.getId());

        // if project is not found
        if (projectOptional.isEmpty()) {
            throw new NoSuchElementFoundException(String.format("Project not found with id %s and current user", projectId));
        }

        Project project = projectOptional.get();


        // build a set of data source ids from list
        Set<String> dataSourceIdSetFromProject = project.getDataSources().stream().map(DataSource::getId).collect(Collectors.toSet());

        List<String> alreadyLinkedDataSourceIds = new ArrayList<>();
        List<String> nonExistingDataSourceIds = new ArrayList<>();
        List<String> forbiddenDataSourceIds = new ArrayList<>();

        List<String> tobeLinkedDataSourceIds = new ArrayList<>();

        // add data source ids to project
        dataSourceIds.forEach((String dataSourceId) -> {
            if (dataSourceIdSetFromProject.contains(dataSourceId)) {
                // record duplicate data source ids
                alreadyLinkedDataSourceIds.add(dataSourceId);
                return;
            }
            Optional<DataSource> dataSource = dataSourceDao.findById(dataSourceId);
            if (dataSource.isEmpty()) {
                // record non-existing data source ids
                nonExistingDataSourceIds.add(dataSourceId);
                return;
            }

            if (!dataSource.get().getCreatedBy().getId().equals(user.getId())) {
                // record non-existing data source ids
                forbiddenDataSourceIds.add(dataSourceId);
                return;
            }

            // add to be added data source ids
            tobeLinkedDataSourceIds.add(dataSourceId);

        });

        // if any error occurred
        if (!alreadyLinkedDataSourceIds.isEmpty() || !nonExistingDataSourceIds.isEmpty() || !forbiddenDataSourceIds.isEmpty()) {
            // build error message
            StringBuilder errorMessage = new StringBuilder();
            if (!alreadyLinkedDataSourceIds.isEmpty()) {
                errorMessage.append("Already linked data source ids: ");
                errorMessage.append(String.join(", ", alreadyLinkedDataSourceIds));
                errorMessage.append(". ");
            }
            if (!nonExistingDataSourceIds.isEmpty()) {
                errorMessage.append("Non-existing data source ids: ");
                errorMessage.append(String.join(", ", nonExistingDataSourceIds));
                errorMessage.append(". ");
            }
            if (!forbiddenDataSourceIds.isEmpty()) {
                errorMessage.append("Forbidden data source ids: ");
                errorMessage.append(String.join(", ", forbiddenDataSourceIds));
                errorMessage.append(". ");
            }
            throw new BadRequestException(errorMessage.toString());
        }

        // obtain data sources from database
        List<DataSource> tobeAddedDataSources = tobeLinkedDataSourceIds
                .stream()
                .map((String dataSourceId) -> {
                    Optional<DataSource> dataSource = dataSourceDao.findById(dataSourceId);
                    assert dataSource.isPresent();
                    return dataSource.get();
                })
                .collect(Collectors.toList());

        // link project to each data source
        tobeAddedDataSources.forEach((dataSource -> {
            dataSource.getProjects().add(project);
            dataSourceDao.save(dataSource);
        }));

        // link each data source to project
        project.getDataSources().addAll(tobeAddedDataSources);
        return projectDao.save(project);
    }

    public @NotNull Project deleteProjectDataSource(String projectId, User user, List<String> dataSourceIds) {
        // find project by id and user id
        Optional<Project> projectOptional = projectDao.findByIdAndUserId(projectId, user.getId());

        // if project is not found
        if (projectOptional.isEmpty()) {
            throw new NoSuchElementFoundException(String.format("Project not found with id %s and current user", projectId));
        }

        Project project = projectOptional.get();

        // build a set of data source ids from list
        Set<String> dataSourceIdSetFromProject = project.getDataSources().stream().map(DataSource::getId).collect(Collectors.toSet());

        List<String> alreadyUnLinkedDataSourceIds = new ArrayList<>();
        List<String> nonExistingDataSourceIds = new ArrayList<>();
        List<String> forbiddenDataSourceIds = new ArrayList<>();

        Set<String> toBeUnlinkedDataSourceIds = new HashSet<>();

        // remove data source ids from project
        dataSourceIds.forEach((String dataSourceId) -> {
            if (!dataSourceIdSetFromProject.contains(dataSourceId)) {
                // record unlinked data source ids
                alreadyUnLinkedDataSourceIds.add(dataSourceId);
                return;
            }
            Optional<DataSource> dataSource = dataSourceDao.findById(dataSourceId);
            if (dataSource.isEmpty()) {
                // record non-existing data source ids
                nonExistingDataSourceIds.add(dataSourceId);
                return;
            }

            if (!dataSource.get().getCreatedBy().getId().equals(user.getId())) {
                // record non-existing data source ids
                forbiddenDataSourceIds.add(dataSourceId);
                return;
            }

            // add data source to be removed from project
            toBeUnlinkedDataSourceIds.add(dataSourceId);
        });

        // if any error occurred
        // TODO: make a JSON response?
        if (!alreadyUnLinkedDataSourceIds.isEmpty() || !nonExistingDataSourceIds.isEmpty() || !forbiddenDataSourceIds.isEmpty()) {
            // build error message
            StringBuilder errorMessage = new StringBuilder();
            if (!alreadyUnLinkedDataSourceIds.isEmpty()) {
                errorMessage.append("Already unlinked data source ids: ");
                errorMessage.append(String.join(", ", alreadyUnLinkedDataSourceIds));
                errorMessage.append(". ");
            }
            if (!nonExistingDataSourceIds.isEmpty()) {
                errorMessage.append("Non-existing data source ids: ");
                errorMessage.append(String.join(", ", nonExistingDataSourceIds));
                errorMessage.append(". ");
            }
            if (!forbiddenDataSourceIds.isEmpty()) {
                errorMessage.append("Forbidden data source ids: ");
                errorMessage.append(String.join(", ", forbiddenDataSourceIds));
                errorMessage.append(". ");
            }
            throw new BadRequestException(errorMessage.toString());
        }

        // unlink project from each data source
        toBeUnlinkedDataSourceIds.forEach(
                (toBeDeletedDataSourceId) -> {
                    dataSourceDao.findById(toBeDeletedDataSourceId).ifPresent(
                            (dataSource) -> {
                                dataSource.getProjects().remove(project);
                                dataSourceDao.save(dataSource);
                            }
                    );
                }
        );

        // unlink data source from project
        project.setDataSources(project.getDataSources().stream().filter(
                dataSource -> !toBeUnlinkedDataSourceIds.contains(dataSource.getId())
        ).collect(Collectors.toList()));

        return projectDao.save(project);
    }
}
