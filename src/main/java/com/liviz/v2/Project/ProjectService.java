package com.liviz.v2.Project;

import com.liviz.v2.Project.ProjectDto;
import com.liviz.v2.Project.ProjectEditingDto;
import com.liviz.v2.Project.Project;
import com.liviz.v2.User.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ProjectService {
    @NotNull Project findByIdAndUserId(String id, String userId);

    @NotNull Project editProject(String id, ProjectEditingDto projectEditingDto, User user);

    void deleteByIdAndUserId(String id, String userId);

    @NotNull List<Project> getProjectsByFilters(User user, Boolean isPublic, String createdById);

    Project cloneProject(String projectId, User user);

    @NotNull Project addProjectDisplaySchema(String id, @NotNull User user, String displaySchemaId);

    @NotNull Project deleteProjectDisplaySchema(String projectId, @NotNull User user);

    @NotNull Project addProjectDataSource(String projectId, User user, List<String> dataSourceIds);

    @NotNull Project deleteProjectDataSource(String projectId, User user, List<String> dataSourceIds);

    @NotNull Project createProject(ProjectDto project, User user);
}
