package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dao.DataSourceDao;
import com.liviz.v2.dao.UserDao;
import com.liviz.v2.dto.DataSourceDto;
import com.liviz.v2.model.DataSource;
import com.liviz.v2.model.User;
import com.liviz.v2.service.UserService;
import javafx.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/data_sources")
public class DataSourceController {
    @Autowired
    DataSourceDao dataSourceDao;

    @Autowired
    UserService userService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    private final Log logger = LogFactory.getLog(getClass());

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

    @GetMapping()
    public ResponseEntity<List<DataSource>> getAllDataSources(@RequestHeader("Authorization") String authorizationHeader) {
        // get jwt user
        User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // TODO: check if user is the creator of the data source

        List<DataSource> dataSources = dataSourceDao.findAll();
        if (dataSources.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(dataSources, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<DataSource> createDataSource(@RequestBody DataSourceDto dataSourceDto,
                                                       @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // get jwt user
            User user = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

            // create new data source
            DataSource dataSource =
                    new DataSource(dataSourceDto.getName(), dataSourceDto.isPublic(), dataSourceDto.getDescription(),
                            dataSourceDto.getStaticData(), dataSourceDto.getDataType(), dataSourceDto.getUrl(),
                            dataSourceDto.getSlots());
            dataSource.setCreatedBy(user);

            // save data source
            dataSource = dataSourceDao.save(dataSource);

            // return data source
            return new ResponseEntity<>(dataSource, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
