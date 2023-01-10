package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dto.ProjectDto;
import com.liviz.v2.dto.ProjectEditingDto;
import com.liviz.v2.dto.ProjectPutDataSourceDto;
import com.liviz.v2.dto.ProjectPutDisplaySchemaDto;
import com.liviz.v2.model.Project;
import com.liviz.v2.model.User;
import com.liviz.v2.serviceImpl.ProjectServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    ProjectServiceImpl projectService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    private final Log logger = LogFactory.getLog(getClass());

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable("id") String id,
                                                  @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Project project = projectService.findByIdAndUserId(id, user.getId());

        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Project> createProject(@Valid @RequestBody ProjectDto project,
                                                 @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Project savedProject = projectService.createProject(project, user);
        return new ResponseEntity<>(savedProject, HttpStatus.CREATED);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<Project> editProject(@PathVariable("id") String id,
                                               @RequestBody ProjectEditingDto projectEditingDto,
                                               @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Project savedProject = projectService.editProject(id, projectEditingDto, user);

        return new ResponseEntity<>(savedProject, HttpStatus.OK);
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

        Project project = projectService.addProjectDisplaySchema(id, user, projectPutDisplaySchemaDto.getDisplaySchemaId());

        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/display_schema")
    public ResponseEntity<Project> deleteProjectDisplaySchema(@PathVariable("id") String projectId,
                                                              @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Project project = projectService.deleteProjectDisplaySchema(projectId, user);

        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @PatchMapping("/{id}/data_sources")
    public ResponseEntity<Project> addProjectDataSource(@PathVariable("id") String projectId,
                                                        @Valid @RequestBody ProjectPutDataSourceDto projectPutDataSourceDto,
                                                        @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Project project = projectService.addProjectDataSource(projectId, user, projectPutDataSourceDto.getDataSourceIds());

        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/data_sources")
    public ResponseEntity<Project> deleteProjectDataSource(@PathVariable("id") String projectId,
                                                           @Valid @RequestBody ProjectPutDataSourceDto projectPutDataSourceDto,
                                                           @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        Project project = projectService.deleteProjectDataSource(projectId, user, projectPutDataSourceDto.getDataSourceIds());

        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    // TODO: try Test Driven Development?


}
