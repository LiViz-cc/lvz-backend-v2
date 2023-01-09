package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dto.DataSourceDto;
import com.liviz.v2.model.DataSource;
import com.liviz.v2.model.User;
import com.liviz.v2.service.DataSourceService;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/data_sources")
public class DataSourceController {

    @Autowired
    DataSourceService dataSourceService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Log logger;

    // TODO: refract all controller methods to services
    @GetMapping("/{id}")
    public ResponseEntity<DataSource> getDataSourceById(@PathVariable("id") String id,
                                                        @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        DataSource dataSourceData = dataSourceService.getDataSource(id, user);

        // return data source
        return new ResponseEntity<>(dataSourceData, HttpStatus.OK);
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

    @PostMapping("/clone")
    public ResponseEntity<DataSource> cloneDataSource(@RequestParam("id") String id,
                                                      @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // clone data source
        DataSource dataSource = dataSourceService.cloneDataSource(id, user);

        // return data source
        return new ResponseEntity<>(dataSource, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataSource> updateDataSource(@PathVariable("id") String id,
                                                       @Valid @RequestBody DataSourceDto dataSourceDto,
                                                       @RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // update data source
        DataSource dataSource = dataSourceService.updateDataSource(id, dataSourceDto, user);

        // return data source
        return new ResponseEntity<>(dataSource, HttpStatus.OK);
    }
}
