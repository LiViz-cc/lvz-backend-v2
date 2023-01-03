package com.liviz.v2.service;

import com.liviz.v2.dao.ProjectDao;
import com.liviz.v2.exception.BadRequestException;
import com.liviz.v2.exception.NoSuchElementFoundException;
import com.liviz.v2.model.Project;
import com.liviz.v2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    ProjectDao projectDao;

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
}
