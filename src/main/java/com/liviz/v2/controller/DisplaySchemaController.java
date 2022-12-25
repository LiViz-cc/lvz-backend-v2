package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dao.UserDao;
import com.liviz.v2.dto.DisplaySchemaDto;
import com.liviz.v2.exception.NoSuchElementFoundException;
import com.liviz.v2.model.DisplaySchema;
import com.liviz.v2.model.User;
import com.liviz.v2.service.DisplaySchemaService;
import com.liviz.v2.service.UserService;
import javafx.util.Pair;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/display_schemas")
public class DisplaySchemaController {
    @Autowired
    DisplaySchemaService displaySchemaService;

    @Autowired
    UserService userService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    Log logger;

    @PostMapping
    public ResponseEntity<DisplaySchema> createDisplaySchema(@RequestBody DisplaySchemaDto displaySchemaDto,
                                                             @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // get jwt user
            Pair<User, HttpStatus> userAndStatus = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);
            User user = userAndStatus.getKey();
            HttpStatus status = userAndStatus.getValue();

            // return unauthenticated if jwt username is null
            if (user == null) {
                return new ResponseEntity<>(status);
            }

            // create display schema
            DisplaySchema savedDisplaySchema = displaySchemaService.createDisplaySchema(displaySchemaDto, user);

            // return created display schema
            return new ResponseEntity<>(savedDisplaySchema, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisplaySchema> getDisplaySchema(@PathVariable("id") String id,
                                                          @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        Pair<User, HttpStatus> userAndStatus = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);
        User user = userAndStatus.getKey();
        HttpStatus status = userAndStatus.getValue();

        // return unauthenticated if jwt username is null
        if (user == null) {
            return new ResponseEntity<>(status);
        }

        // search display schema by id and user id
        DisplaySchema displaySchema = displaySchemaService.getDisplaySchema(id, user);

        if (displaySchema == null) {
            throw new NoSuchElementFoundException(String.format("No display schema found with id %s and current user", id));
        }

        // return display schema
        return new ResponseEntity<>(displaySchema, HttpStatus.OK);
    }

}
