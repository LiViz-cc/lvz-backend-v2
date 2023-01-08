package com.liviz.v2.service;

import com.liviz.v2.dao.ProjectDao;
import com.liviz.v2.dao.ShareConfigDao;
import com.liviz.v2.dto.DisplaySchemaChangePasswordDto;
import com.liviz.v2.dto.ShareConfigDto;
import com.liviz.v2.exception.NoSuchElementFoundException;
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

    public Optional<ShareConfig> getShareConfigByIdAndUser(String id, User user) {
        return shareConfigDao.findByIdAndUserId(id, user.getId());
    }

    public void deleteShareConfigByIdAndUser(String id, User user) {
        // check if share config exists
        Optional<ShareConfig> shareConfigOptional = getShareConfigByIdAndUser(id, user);
        if (shareConfigOptional.isEmpty()) {
            throw new RuntimeException("Share config not found");
        }

        // delete share config
        shareConfigDao.delete(shareConfigOptional.get());
    }

    public ShareConfig changePassword(String shareConfigId, DisplaySchemaChangePasswordDto displaySchemaChangePasswordDto, User user) {
        // check if share config exists
        Optional<ShareConfig> shareConfigOptional = getShareConfigByIdAndUser(shareConfigId, user);
        if (shareConfigOptional.isEmpty()) {
            throw new NoSuchElementFoundException("Share config with id " + shareConfigId + " not found");
        }

        ShareConfig shareConfig = shareConfigOptional.get();

        // check if old password is correct
        if (shareConfig.getPasswordProtected() && !shareConfig.getPassword().equals(displaySchemaChangePasswordDto.getOldPassword())) {
            System.out.println("shareConfig.getPassword() = " + shareConfig.getPassword());
            System.out.println("displaySchemaChangePasswordDto.getOldPassword() = " + displaySchemaChangePasswordDto.getOldPassword());
            throw new RuntimeException("Old password is incorrect");
        }

        // change password
        shareConfig.setPassword(displaySchemaChangePasswordDto.getNewPassword());

        if (displaySchemaChangePasswordDto.getNewPassword() == null || displaySchemaChangePasswordDto.getNewPassword().isEmpty()) {
            shareConfig.setPasswordProtected(false);
        } else {
            shareConfig.setPasswordProtected(true);
        }

        // save share config
        return shareConfigDao.save(shareConfig);

    }
}
