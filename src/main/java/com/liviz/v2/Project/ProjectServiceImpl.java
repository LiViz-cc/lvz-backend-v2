package com.liviz.v2.Project;

import com.liviz.v2.DataSource.DataSourceDao;
import com.liviz.v2.DisplaySchema.DisplaySchemaDao;
import com.liviz.v2.Project.ProjectDao;
import com.liviz.v2.Project.ProjectDto;
import com.liviz.v2.Project.ProjectEditingDto;
import com.liviz.v2.exception.BadRequestException;
import com.liviz.v2.exception.NoSuchElementFoundException;
import com.liviz.v2.DataSource.DataSource;
import com.liviz.v2.DisplaySchema.DisplaySchema;
import com.liviz.v2.Project.Project;
import com.liviz.v2.User.User;
import com.liviz.v2.Project.ProjectService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    ProjectDao projectDao;

    @Autowired
    DisplaySchemaDao displaySchemaDao;

    @Autowired
    DataSourceDao dataSourceDao;


    @Override
    public @NotNull Project findByIdAndUserId(String id, String userId) {
        Optional<Project> projectOptional = projectDao.findByIdAndUserId(id, userId);

        // check if project exists
        if (projectOptional.isEmpty()) {
            throw new NoSuchElementFoundException("Project with id " + id + " not found");
        }

        return projectOptional.get();
    }

    @Override
    public @NotNull Project editProject(String id, ProjectEditingDto projectEditingDto, User user) {
        Project project = findByIdAndUserId(id, user.getId());

        // update project
        if (projectEditingDto.getName() != null) {
            project.setName(projectEditingDto.getName());
        }

        if (projectEditingDto.getDescription() != null) {
            project.setDescription(projectEditingDto.getDescription());
        }

        if (projectEditingDto.getIsPublic() != null) {
            project.setIsPublic(projectEditingDto.getIsPublic());
        }

        project.setModifiedTime(new Date());

        return projectDao.save(project);
    }

    @Override
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

    @Override
    public @NotNull List<Project> getProjectsByFilters(User user, Boolean isPublic, String createdById) {
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

    @Override
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

        // set time
        newProject.setCreatedTime(new Date());
        newProject.setModifiedTime(new Date());

        // clean linkage
        newProject.setDisplaySchema(null);
        newProject.setDataSources(new ArrayList<>());

        // save and return new project
        return projectDao.save(newProject);
    }

    @Override
    @NotNull
    public Project addProjectDisplaySchema(String id, @NotNull User user, String displaySchemaId) {
        // find project by id and user id
        Optional<Project> projectOptional = projectDao.findByIdAndUserId(id, user.getId());

        // if project is not found
        if (projectOptional.isEmpty()) {
            throw new NoSuchElementFoundException(String.format("Project not found with id %s and current user", id));
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
        return projectDao.save(project);
    }


    @Override
    @NotNull
    public Project deleteProjectDisplaySchema(String projectId, @NotNull User user) {
        // find project by id and user id
        Optional<Project> projectOptional = projectDao.findByIdAndUserId(projectId, user.getId());

        // if project is not found
        if (projectOptional.isEmpty()) {
            throw new NoSuchElementFoundException(String.format("Project not found with id %s and current user", projectId));
        }

        Project project = projectOptional.get();

        // remove display schema from project and vice versa
        if (project.getDisplaySchema() != null) {
            project.getDisplaySchema().setLinkedProject(null);
            displaySchemaDao.save(project.getDisplaySchema());
        }
        project.setDisplaySchema(null);

        // save and return project
        return projectDao.save(project);
    }

    @Override
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

    @Override
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

    @Override
    public @NotNull Project createProject(ProjectDto project, User user) {
        return projectDao.save(
                new Project(
                        project.getName(), new Date(), new Date(), user, project.getIsPublic(),
                        project.getDescription()));
    }
}
