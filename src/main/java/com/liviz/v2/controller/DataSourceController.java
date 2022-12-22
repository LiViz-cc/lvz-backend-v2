package com.liviz.v2.controller;

import com.liviz.v2.dao.DataSourceDao;
import com.liviz.v2.dao.UserDao;
import com.liviz.v2.dto.DataSourcePostingDto;
import com.liviz.v2.model.DataSource;
import com.liviz.v2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class DataSourceController {
    @Autowired
    DataSourceDao dataSourceDao;

    @Autowired
    UserDao userDao;

    @GetMapping("/data_sources/{id}")
    public ResponseEntity<DataSource> getDataSourceById(@PathVariable("id") String id) {

        Optional<DataSource> dataSourceData = dataSourceDao.findById((id));
        if (dataSourceData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(dataSourceData.get(), HttpStatus.OK);
    }

    @GetMapping("/data_sources")
    public ResponseEntity<List<DataSource>> getAllDataSources() {
        List<DataSource> dataSources = dataSourceDao.findAll();
        if (dataSources.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(dataSources, HttpStatus.OK);
    }

    @PostMapping("/data_sources")
    public ResponseEntity<DataSource> createDataSource(@RequestBody DataSourcePostingDto dataSourceDto) {
        try {
            User user = userDao.findById("63a16de74b87ded4ae350dbd").get();
            DataSource dataSource =
                    new DataSource(dataSourceDto.getName(), dataSourceDto.getIsPublic(), dataSourceDto.getDescription(),
                            dataSourceDto.getStatic_data(), dataSourceDto.getData_type(), dataSourceDto.getUrl());
            dataSource.setUser(user);
            dataSource = dataSourceDao.save(dataSource);
            return new ResponseEntity<>(dataSource, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
