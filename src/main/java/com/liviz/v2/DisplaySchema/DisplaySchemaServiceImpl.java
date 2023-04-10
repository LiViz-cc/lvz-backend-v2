package com.liviz.v2.DisplaySchema;

import com.liviz.v2.DisplaySchema.DisplaySchemaDao;
import com.liviz.v2.Project.ProjectDao;
import com.liviz.v2.DisplaySchema.DisplaySchemaDto;
import com.liviz.v2.exception.BadRequestException;
import com.liviz.v2.exception.NoSuchElementFoundException;
import com.liviz.v2.DisplaySchema.DisplaySchema;
import com.liviz.v2.Project.Project;
import com.liviz.v2.User.User;
import com.liviz.v2.DisplaySchema.DisplaySchemaService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DisplaySchemaServiceImpl implements DisplaySchemaService {
    @Autowired
    DisplaySchemaDao displaySchemaDao;

    @Autowired
    ProjectDao projectDao;

    @Override
    public @NotNull DisplaySchema createDisplaySchema(DisplaySchemaDto displaySchemaDto, User user) {
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

    @Override
    public @NotNull DisplaySchema getDisplaySchema(String id, User user) {
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

    @Override
    public @NotNull DisplaySchema updateDisplaySchema(DisplaySchema displaySchema, DisplaySchemaDto displaySchemaDto, User user) {
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

    @Override
    public void deleteDisplaySchema(String displaySchemaId, User user) {
        // get display schema
        Optional<DisplaySchema> displaySchemaOptional = displaySchemaDao.findByIdAndUserId(displaySchemaId, user.getId());

        // return not found if display schema is not found
        if (displaySchemaOptional.isEmpty()) {
            throw new NoSuchElementFoundException(String.format("Display schema not found with id %s and current user", displaySchemaId));
        }

        // check linked project with display schema
        if (displaySchemaOptional.get().getLinkedProject() != null) {
            Project linkedProject = displaySchemaOptional.get().getLinkedProject();
            linkedProject.setDisplaySchema(null);
            projectDao.save(linkedProject);
        }

        // delete display schema
        displaySchemaDao.delete(displaySchemaOptional.get());
    }

    @Override
    public @NotNull List<DisplaySchema> getDisplaySchemas(User user, Boolean isPublic, String createdById) {
        // if requested user is not the jwt user
        if (user == null || !user.getId().equals(createdById)) {
            // if isPublic is not True
            if (isPublic == null || !isPublic) {
                throw new BadRequestException("This query combination is not allowed.");
            }
        }

        if (isPublic != null && createdById != null) {
            return displaySchemaDao.queryByFilters(isPublic, createdById);
        } else if (isPublic != null) {
            return displaySchemaDao.queryByIsPublic(isPublic);
        } else if (createdById != null) {
            return displaySchemaDao.queryByCreatedBy(createdById);
        }

        throw new BadRequestException("This query combination is not allowed.");


    }
}
