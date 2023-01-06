package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dao.ProjectDao;
import com.liviz.v2.dao.UserDao;
import com.liviz.v2.dto.ProjectDto;
import com.liviz.v2.dto.ProjectEditingDto;
import com.liviz.v2.dto.ProjectPutDataSourceDto;
import com.liviz.v2.dto.ProjectPutDisplaySchemaDto;
import com.liviz.v2.exception.NoSuchElementFoundException;
import com.liviz.v2.model.Project;
import com.liviz.v2.model.User;
import com.liviz.v2.service.ProjectService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectDao projectDao;

    @Autowired
    UserDao userDao;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    private final Log logger = LogFactory.getLog(getClass());

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable("id") String id,
                                                  @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Optional<Project> projectData = projectService.findByIdAndUserId(id, user.getId());

        if (projectData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(projectData.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Project> createProject(@Valid @RequestBody ProjectDto project,
                                                 @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Project savedProject = projectDao.save(
                new Project(
                        project.getName(), new Date(), new Date(), user, project.getIsPublic(),
                        project.getDescription()));
        return new ResponseEntity<>(savedProject, HttpStatus.CREATED);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable("id") String id,
                                                 @RequestBody ProjectEditingDto projectEditingDto,
                                                 @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Optional<Project> projectOptional = projectService.findByIdAndUserId(id, user.getId());

        if (projectOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // update project
        Project project = projectOptional.get();

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

        return new ResponseEntity<>(projectDao.save(project), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProject(@PathVariable("id") String id,
                                                    @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        projectService.deleteByIdAndUserId(id, user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping
    public ResponseEntity<Iterable<Project>> getProjectsByFilters(@RequestHeader("Authorization") String authorizationHeader,
                                                                  @RequestParam(name = "is_public", required = false) Boolean isPublic,
                                                                  @RequestParam(name = "created_by", required = false) String createdBy) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        List<Project> projects = projectService.getProjectsByFilters(user, isPublic, createdBy);

        if (projects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @PostMapping("/clone")
    public ResponseEntity<Project> cloneProject(@RequestParam("id") String id,
                                                @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Project project = projectService.cloneProject(id, user);

        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    // TODO: revise api design
    @PutMapping("/{id}/display_schema")
    public ResponseEntity<Project> addProjectDisplaySchema(@PathVariable("id") String id,
                                                           @Valid @RequestBody ProjectPutDisplaySchemaDto projectPutDisplaySchemaDto,
                                                           @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Optional<Project> projectOptional = projectService.addProjectDisplaySchema(id, user, projectPutDisplaySchemaDto.getDisplaySchemaId());

        if (projectOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(projectDao.save(projectOptional.get()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/display_schema")
    public ResponseEntity<Project> deleteProjectDisplaySchema(@PathVariable("id") String projectId,
                                                              @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Optional<Project> projectOptional = projectService.deleteProjectDisplaySchema(projectId, user);

        if (projectOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(projectDao.save(projectOptional.get()), HttpStatus.OK);
    }

    // TODO: use Patch instead of Put
    @PatchMapping("/{id}/data_sources")
    public ResponseEntity<Project> addProjectDataSource(@PathVariable("id") String projectId,
                                                        @Valid @RequestBody ProjectPutDataSourceDto projectPutDataSourceDto,
                                                        @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Project project = projectService.addProjectDataSource(projectId, user, projectPutDataSourceDto.getDataSourceIds());

        return new ResponseEntity<>(projectDao.save(project), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/data_sources")
    public ResponseEntity<Project> deleteProjectDataSource(@PathVariable("id") String projectId,
                                                           @Valid @RequestBody ProjectPutDataSourceDto projectPutDataSourceDto,
                                                           @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Project project = projectService.deleteProjectDataSource(projectId, user, projectPutDataSourceDto.getDataSourceIds());

        return new ResponseEntity<>(projectDao.save(project), HttpStatus.OK);
    }


}
