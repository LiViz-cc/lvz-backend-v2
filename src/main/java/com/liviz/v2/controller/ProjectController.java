package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dao.ProjectDao;
import com.liviz.v2.dao.UserDao;
import com.liviz.v2.dto.ProjectDto;
import com.liviz.v2.dto.ProjectEditingDto;
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
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(projectData.get(), HttpStatus.OK);
    }

    // TODO: remove all try-catch blocks in controllers, services and dao
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

    @PutMapping("/{id}")
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

}
