package com.liviz.v2.service;

import com.liviz.v2.dao.ProjectDao;
import com.liviz.v2.dao.ShareConfigDao;
import com.liviz.v2.dto.DisplaySchemaDto;
import com.liviz.v2.dto.ShareConfigDto;
import com.liviz.v2.model.Project;
import com.liviz.v2.model.ShareConfig;
import com.liviz.v2.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ShareConfigService {
    @Autowired
    ShareConfigDao shareConfigDao;

    @Autowired
    ProjectDao projectDao;

    public ShareConfig createShareConfig(@NotNull ShareConfigDto shareConfigDto, @NotNull User user) {
        Optional<Project> linkedProjectOptional = projectDao.findByIdAndUserId(
                shareConfigDto.getLinkedProjectId(), user.getId());
        if (linkedProjectOptional.isEmpty()) {
            throw new RuntimeException("Project not found");
        }

        ShareConfig shareConfig = new ShareConfig(
                null,
                shareConfigDto.getName(),
                new Date(),
                new Date(),
                user,
                linkedProjectOptional.get(),
                shareConfigDto.getDescription(),
                shareConfigDto.getPasswordProtected(),
                shareConfigDto.getPassword()
        );
        return shareConfigDao.save(shareConfig);
    }
}
