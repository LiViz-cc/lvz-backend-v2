package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dao.UserDao;
import com.liviz.v2.dto.DisplaySchemaDto;
import com.liviz.v2.model.DisplaySchema;
import com.liviz.v2.model.User;
import com.liviz.v2.service.DisplaySchemaService;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class DisplaySchemaController {
    @Autowired
    DisplaySchemaService displaySchemaService;

    @Autowired
    UserDao userDao;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    Log logger;

    @PostMapping("/display_schemas")
    public ResponseEntity<DisplaySchema> createDisplaySchema(@RequestBody DisplaySchemaDto displaySchemaDto,
                                                             @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // get jwt username
            String usernameFromToken = jwtTokenUtil.getJwtIdentity(authorizationHeader);

            // return unauthenticated if jwt username is null
            Optional<User> userOptional = userDao.findByUsername(usernameFromToken);
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            }
            User user = userOptional.get();

            return displaySchemaService.createDisplaySchema(displaySchemaDto, user);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
