package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dao.ProjectDao;
import com.liviz.v2.dao.UserDao;
import com.liviz.v2.model.Project;
import com.liviz.v2.model.User;
import com.liviz.v2.service.ProjectService;
import javafx.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Project> getUserById(@PathVariable("id") String id,
                                               @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        Pair<User, HttpStatus> userAndStatus = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);
        User user = userAndStatus.getKey();
        HttpStatus status = userAndStatus.getValue();

        // return unauthenticated if jwt username is null
        if (user == null) {
            return new ResponseEntity<>(status);
        }

        Optional<Project> projectData = projectService.findByIdAndUserId(id, user.getId());

        if (projectData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(projectData.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Project> createProject(@RequestBody Project project,
                                                 @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        Pair<User, HttpStatus> userAndStatus = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);
        User user = userAndStatus.getKey();
        HttpStatus status = userAndStatus.getValue();

        // return unauthenticated if jwt username is null
        if (user == null) {
            return new ResponseEntity<>(status);
        }

        try {
            Project savedProject = projectDao.save(
                    new Project(
                            project.getName(), new Date(), new Date(), user, project.isPublic(),
                            project.getDescription(), project.getDataSources()));
            return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
