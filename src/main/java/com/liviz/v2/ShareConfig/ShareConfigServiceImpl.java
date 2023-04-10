package com.liviz.v2.ShareConfig;

import com.liviz.v2.Project.ProjectDao;
import com.liviz.v2.ShareConfig.ShareConfigDao;
import com.liviz.v2.DisplaySchema.DisplaySchemaChangePasswordDto;
import com.liviz.v2.ShareConfig.ShareConfigDto;
import com.liviz.v2.exception.BadRequestException;
import com.liviz.v2.exception.NoSuchElementFoundException;
import com.liviz.v2.Project.Project;
import com.liviz.v2.ShareConfig.ShareConfig;
import com.liviz.v2.User.User;
import com.liviz.v2.ShareConfig.ShareConfigService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShareConfigServiceImpl implements ShareConfigService {
    @Autowired
    ShareConfigDao shareConfigDao;

    @Autowired
    ProjectDao projectDao;
    private Optional<ShareConfig> shareConfigOptional;


    @Override
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

        // link project
        Optional<Project> projectData = projectDao.findByIdAndUserId(shareConfigDto.getLinkedProjectId(), user.getId());

        if (projectData.isEmpty()) {
            throw new NoSuchElementFoundException("Project with id " + shareConfigDto.getLinkedProjectId() + " not found");
        }

        // save share config
        shareConfigDao.save(shareConfig);

        // link project with share config
        projectData.get().addShareConfig(shareConfig);
        projectDao.save(projectData.get());

        // return share config
        return shareConfigDao.save(shareConfig);

    }


    @Override
    public ShareConfig getShareConfigByIdAndUser(String id, User user) {
        Optional<ShareConfig> shareConfigOptional = shareConfigDao.findByIdAndUserId(id, user.getId());

        if (shareConfigOptional.isEmpty()) {
            throw new NoSuchElementFoundException("Share config with id " + id + " not found");
        }

        return shareConfigOptional.get();
    }

    @Override
    public void deleteShareConfigByIdAndUser(String id, User user) {
        // check if share config exists
        ShareConfig shareConfig = getShareConfigByIdAndUser(id, user);

        // unlink project
        if (shareConfig.getLinkedProject() != null) {
            shareConfig.getLinkedProject().removeShareConfig(shareConfig);
            projectDao.save(shareConfig.getLinkedProject());
        }

        // delete share config
        shareConfigDao.delete(shareConfig);
    }

    @Override
    public ShareConfig changePassword(String shareConfigId, DisplaySchemaChangePasswordDto displaySchemaChangePasswordDto, User user) {
        // check if share config exists
        ShareConfig shareConfig = getShareConfigByIdAndUser(shareConfigId, user);

        // check if old password is correct
        if (shareConfig.getPasswordProtected() && !shareConfig.getPassword().equals(displaySchemaChangePasswordDto.getOldPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // change password
        shareConfig.setPassword(displaySchemaChangePasswordDto.getNewPassword());

        shareConfig.setPasswordProtected(displaySchemaChangePasswordDto.getNewPassword() != null && !displaySchemaChangePasswordDto.getNewPassword().isEmpty());

        // save share config
        return shareConfigDao.save(shareConfig);
    }

    @Override
    public ShareConfig updateShareConfig(String shareConfigId, ShareConfigDto shareConfigDto, User user) {
        // delete old share config
        deleteShareConfigByIdAndUser(shareConfigId, user);

        // create new share config
        return createShareConfig(shareConfigDto, user);
    }

    @Override
    public List<ShareConfig> getShareConfigsByFilter(User user, String createdById) {
        // if requested user is not the jwt user
        if (user == null || !user.getId().equals(createdById)) {
            throw new BadRequestException("This query combination is not allowed.");
        }

        return shareConfigDao.queryByCreatedBy(createdById);
    }
}
