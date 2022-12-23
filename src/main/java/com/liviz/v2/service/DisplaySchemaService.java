package com.liviz.v2.service;

import com.liviz.v2.dao.DisplaySchemaDao;
import com.liviz.v2.dto.DisplaySchemaDto;
import com.liviz.v2.model.DisplaySchema;
import com.liviz.v2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DisplaySchemaService {
    @Autowired
    DisplaySchemaDao displaySchemaDao;

    public ResponseEntity<DisplaySchema> createDisplaySchema(DisplaySchemaDto displaySchemaDto, User user) {
        // create new display schema
        DisplaySchema displaySchema = new DisplaySchema(displaySchemaDto.getName(), new Date(), new Date(), user, displaySchemaDto.getIsPublic(), displaySchemaDto.getDescription(),
                displaySchemaDto.getEChartOption(), displaySchemaDto.getLinkedProject());

        // save display schema
        DisplaySchema savedDisplaySchema = displaySchemaDao.save(displaySchema);

        // return display schema
        return new ResponseEntity<>(savedDisplaySchema, HttpStatus.CREATED);
    }
}
