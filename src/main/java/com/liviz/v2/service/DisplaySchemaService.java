package com.liviz.v2.service;

import com.liviz.v2.dto.DisplaySchemaDto;
import com.liviz.v2.model.DisplaySchema;
import com.liviz.v2.model.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DisplaySchemaService {
    @NotNull DisplaySchema createDisplaySchema(DisplaySchemaDto displaySchemaDto, User user);

    @NotNull DisplaySchema getDisplaySchema(String id, User user);

    @NotNull DisplaySchema updateDisplaySchema(DisplaySchema displaySchema, DisplaySchemaDto displaySchemaDto, User user);

    void deleteDisplaySchema(String displaySchemaId, User user);

    @NotNull List<DisplaySchema> getDisplaySchemas(User user, Boolean isPublic, String createdById);
}
