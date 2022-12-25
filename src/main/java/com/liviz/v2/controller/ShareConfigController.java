package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dto.DisplaySchemaDto;
import com.liviz.v2.dto.ShareConfigDto;
import com.liviz.v2.model.DisplaySchema;
import com.liviz.v2.model.ShareConfig;
import com.liviz.v2.model.User;
import com.liviz.v2.service.ShareConfigService;
import javafx.util.Pair;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/share_configs")
public class ShareConfigController {
    @Autowired
    ShareConfigService shareConfigService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    Log logger;

    @PostMapping
    public ResponseEntity<ShareConfig> createShareConfig(@RequestBody ShareConfigDto shareConfigDto,
                                                         @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // get jwt user
            User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

            // create share config
            ShareConfig savedShareConfig = shareConfigService.createShareConfig(shareConfigDto, user);

            // return created share config
            return new ResponseEntity<>(savedShareConfig, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShareConfig> getShareConfig(@PathVariable("id") String id,
                                                      @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // get jwt user
            User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

            // get share config
            Optional<ShareConfig> shareConfigOptional = shareConfigService.getShareConfigByIdAndUser(id, user);
            if (shareConfigOptional.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // return share config
            return new ResponseEntity<>(shareConfigOptional.get(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ShareConfig> deleteShareConfig(@PathVariable("id") String id,
                                                         @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // get jwt user
            User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

            // delete share config
            shareConfigService.deleteShareConfigByIdAndUser(id, user);

            // return no content
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
