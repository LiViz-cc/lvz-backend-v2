package com.liviz.v2.controller;

import com.liviz.v2.dao.ProjectDao;
import com.liviz.v2.dao.UserDao;
import com.liviz.v2.model.Project;
import com.liviz.v2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@RestController
public class ProjectController {
    @Autowired
    ProjectDao projectDao;

    @Autowired
    UserDao userDao;

    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getUserById(@PathVariable("id") String id) {
        Optional<Project> projectData = projectDao.findById(id);

        if (projectData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(projectData.get(), HttpStatus.OK);
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Optional<User> userData = userDao.findById("63a16de74b87ded4ae350dbd");
        try {
            Project _project = projectDao.save(new Project(project.getName(),
                    userData.get(), project.isPublic(), project.getDescription()));
            return new ResponseEntity<>(_project, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
