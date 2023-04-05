package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dto.DisplaySchemaChangePasswordDto;
import com.liviz.v2.dto.ShareConfigDto;
import com.liviz.v2.model.ShareConfig;
import com.liviz.v2.model.User;
import com.liviz.v2.service.ShareConfigService;
import com.liviz.v2.serviceImpl.ShareConfigServiceImpl;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public ResponseEntity<ShareConfig> createShareConfig(@Valid @RequestBody ShareConfigDto shareConfigDto,
                                                         @RequestHeader("Authorization") String authorizationHeader) {

        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // create share config
        ShareConfig savedShareConfig = shareConfigService.createShareConfig(shareConfigDto, user);

        // return created share config
        return new ResponseEntity<>(savedShareConfig, HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ShareConfig> getShareConfig(@PathVariable("id") String id,
                                                      @RequestHeader("Authorization") String authorizationHeader) {

        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // get share config
        ShareConfig shareConfig = shareConfigService.getShareConfigByIdAndUser(id, user);

        // return share config
        return new ResponseEntity<>(shareConfig, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ShareConfig> deleteShareConfig(@PathVariable("id") String id,
                                                         @RequestHeader("Authorization") String authorizationHeader) {

        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // delete share config
        shareConfigService.deleteShareConfigByIdAndUser(id, user);

        // return no content
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<ShareConfig> changePassword(@PathVariable("id") String shareConfigId,
                                                      @Valid @RequestBody DisplaySchemaChangePasswordDto displaySchemaChangePasswordDto,
                                                      @RequestHeader("Authorization") String authorizationHeader) {

        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // change password
        ShareConfig shareConfig = shareConfigService.changePassword(shareConfigId, displaySchemaChangePasswordDto, user);

        return new ResponseEntity<>(shareConfig, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShareConfig> updateShareConfig(@PathVariable("id") String id,
                                                         @Valid @RequestBody ShareConfigDto shareConfigDto,
                                                         @RequestHeader("Authorization") String authorizationHeader) {

        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // update share config
        ShareConfig shareConfig = shareConfigService.updateShareConfig(id, shareConfigDto, user);

        // return share config
        return new ResponseEntity<>(shareConfig, HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity<List<ShareConfig>> getShareConfigsByFilter(@RequestHeader("Authorization") String authorizationHeader,
                                                                     @RequestParam(name = "created_by", required = false) String createdBy) {

        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // get share configs by filter
        List<ShareConfig> shareConfigs = shareConfigService.getShareConfigsByFilter(user, createdBy);

        if (shareConfigs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // return share configs
        return new ResponseEntity<>(shareConfigs, HttpStatus.OK);

    }


}
