package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dao.DataSourceDao;
import com.liviz.v2.dao.UserDao;
import com.liviz.v2.dto.DataSourceDto;
import com.liviz.v2.model.DataSource;
import com.liviz.v2.model.User;
import com.liviz.v2.service.DataSourceService;
import com.liviz.v2.service.UserService;
import javafx.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/data_sources")
public class DataSourceController {
    @Autowired
    DataSourceDao dataSourceDao;

    @Autowired
    DataSourceService dataSourceService;

    @Autowired
    UserService userService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Log logger;

    @GetMapping("/{id}")
    public ResponseEntity<DataSource> getDataSourceById(@PathVariable("id") String id,
                                                        @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // return not found if data source is not found
        Optional<DataSource> dataSourceData = dataSourceDao.findById(id);
        if (dataSourceData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // return unauthorized if jwt username is not equal to user id
        if (!dataSourceData.get().getCreatedBy().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // return data source
        return new ResponseEntity<>(dataSourceData.get(), HttpStatus.OK);
    }


    @PostMapping()
    public ResponseEntity<DataSource> createDataSource(@Valid @RequestBody DataSourceDto dataSourceDto,
                                                       @RequestHeader("Authorization") String authorizationHeader) {

        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // create and save data source
        DataSource dataSource = dataSourceService.createDataSource(dataSourceDto, user);

        // return data source
        return new ResponseEntity<>(dataSource, HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<List<DataSource>> getDataSourcesByUserId(@RequestHeader("Authorization") String authorizationHeader,
                                                                   @RequestParam(value = "created_by", required = false) String createdById,
                                                                   @RequestParam(value = "is_public", required = false) Boolean isPublic) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // get data sources
        List<DataSource> dataSources = dataSourceService.findAllByFilters(user, createdById, isPublic);

        // return data sources
        return new ResponseEntity<>(dataSources, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DataSource> deleteDataSource(@PathVariable("id") String id,
                                                       @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // delete data source
        dataSourceService.deleteDataSource(id, user);

        // return no content
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
