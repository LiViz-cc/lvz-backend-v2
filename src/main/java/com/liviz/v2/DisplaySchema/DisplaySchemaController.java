package com.liviz.v2.DisplaySchema;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.exception.NoSuchElementFoundException;
import com.liviz.v2.User.User;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/display_schemas")
public class DisplaySchemaController {
    @Autowired
    DisplaySchemaService displaySchemaService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    Log logger;

    @PostMapping
    public ResponseEntity<DisplaySchema> createDisplaySchema(@Valid @RequestBody DisplaySchemaDto displaySchemaDto,
                                                             @RequestHeader("Authorization") String authorizationHeader) {

        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // create display schema
        DisplaySchema savedDisplaySchema = displaySchemaService.createDisplaySchema(displaySchemaDto, user);

        // return created display schema
        return new ResponseEntity<>(savedDisplaySchema, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisplaySchema> getDisplaySchema(@PathVariable("id") String id,
                                                          @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // search display schema by id and user id
        DisplaySchema displaySchema = displaySchemaService.getDisplaySchema(id, user);

        if (displaySchema == null) {
            throw new NoSuchElementFoundException(String.format("No display schema found with id %s and current user", id));
        }

        // return display schema
        return new ResponseEntity<>(displaySchema, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisplaySchema> updateDisplaySchema(@PathVariable("id") String id,
                                                             @Valid @RequestBody DisplaySchemaDto displaySchemaDto,
                                                             @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // search display schema by id and user id
        DisplaySchema displaySchema = displaySchemaService.getDisplaySchema(id, user);

        if (displaySchema == null) {
            throw new NoSuchElementFoundException(String.format("No display schema found with id %s and current user", id));
        }

        // update display schema
        DisplaySchema updatedDisplaySchema = displaySchemaService.updateDisplaySchema(displaySchema, displaySchemaDto, user);

        // return updated display schema
        return new ResponseEntity<>(updatedDisplaySchema, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DisplaySchema> deleteDisplaySchema(@PathVariable("id") String displaySchemaId,
                                                             @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // delete display schema
        displaySchemaService.deleteDisplaySchema(displaySchemaId, user);

        // return no content
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // TODO: add pagination
    @GetMapping
    public ResponseEntity<Object> getDisplaySchemas(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(name = "is_public", required = false) Boolean isPublic,
            @RequestParam(name = "created_by", required = false) String createdBy
    ) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // get display schemas
        List<DisplaySchema> displaySchemas = displaySchemaService.getDisplaySchemas(user, isPublic, createdBy);

        // return display schemas
        return new ResponseEntity<>(displaySchemas, HttpStatus.OK);

    }


}
