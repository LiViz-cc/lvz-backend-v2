package com.liviz.v2.service;

import com.liviz.v2.dao.DisplaySchemaDao;
import com.liviz.v2.dao.ProjectDao;
import com.liviz.v2.dto.DisplaySchemaDto;
import com.liviz.v2.exception.NoSuchElementFoundException;
import com.liviz.v2.model.DisplaySchema;
import com.liviz.v2.model.Project;
import com.liviz.v2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class DisplaySchemaService {
    @Autowired
    DisplaySchemaDao displaySchemaDao;

    @Autowired
    ProjectDao projectDao;

    public DisplaySchema createDisplaySchema(DisplaySchemaDto displaySchemaDto, User user) {
        // find project by id
        Optional<Project> projectOptional = Optional.empty();
        if (displaySchemaDto.getLinkedProjectId() != null) {
            projectOptional = projectDao.findByIdAndUserId(displaySchemaDto.getLinkedProjectId(), user.getId());
            if (projectOptional.isEmpty()) {
                throw new NoSuchElementFoundException(String.format("Project not found with id %s and current user", displaySchemaDto.getLinkedProjectId()));
            }
        }

        // create new display schema
        DisplaySchema displaySchema = new DisplaySchema(displaySchemaDto.getName(), new Date(), new Date(), user, displaySchemaDto.getIsPublic(), displaySchemaDto.getDescription(),
                displaySchemaDto.getEChartOption(), projectOptional.orElse(null));

        // save and return display schema
        return displaySchemaDao.save(displaySchema);
    }

    public DisplaySchema getDisplaySchema(String id, User user) {
        // get display schema
        Optional<DisplaySchema> displaySchemaOptional = displaySchemaDao.findByIdAndUserId(id, user.getId());

        // return not found if display schema is not found
        if (displaySchemaOptional.isEmpty()) {
            return null;
        }

        // check if display schema is created by user
        if (!displaySchemaOptional.get().getCreatedBy().getId().equals(user.getId())) {
            return null;
        }

        return displaySchemaOptional.get();
    }

    public DisplaySchema updateDisplaySchema(DisplaySchema displaySchema, DisplaySchemaDto displaySchemaDto, User user) {
        if (!displaySchema.getCreatedBy().getId().equals(user.getId())) {
            // TODO: add unauthorized exception (403 forbidden)
            throw new RuntimeException("You are not allowed to update this display schema");
        }

        // find project by id
        Optional<Project> projectOptional = Optional.empty();
        if (displaySchemaDto.getLinkedProjectId() != null) {
            projectOptional = projectDao.findByIdAndUserId(displaySchemaDto.getLinkedProjectId(), user.getId());
            if (projectOptional.isEmpty()) {
                throw new NoSuchElementFoundException(String.format("Project not found with id %s and current user", displaySchemaDto.getLinkedProjectId()));
            }
        }

        // create new display schema
        DisplaySchema newDisplaySchema = new DisplaySchema(displaySchemaDto.getName(), new Date(), new Date(), user, displaySchemaDto.getIsPublic(), displaySchemaDto.getDescription(),
                displaySchemaDto.getEChartOption(), projectOptional.orElse(null));

        newDisplaySchema.setId(displaySchema.getId());

        // save and return display schema
        return displaySchemaDao.save(newDisplaySchema);

    }
}
