package com.liviz.v2.ShareConfig;

import com.liviz.v2.DisplaySchema.DisplaySchemaChangePasswordDto;
import com.liviz.v2.ShareConfig.ShareConfigDto;
import com.liviz.v2.ShareConfig.ShareConfig;
import com.liviz.v2.User.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ShareConfigService {
    ShareConfig createShareConfig(@NotNull ShareConfigDto shareConfigDto, @NotNull User user);

    ShareConfig getShareConfigByIdAndUser(String id, User user);

    void deleteShareConfigByIdAndUser(String id, User user);

    ShareConfig changePassword(String shareConfigId, DisplaySchemaChangePasswordDto displaySchemaChangePasswordDto, User user);

    ShareConfig updateShareConfig(String shareConfigId, ShareConfigDto shareConfigDto, User user);

    List<ShareConfig> getShareConfigsByFilter(User user, String createdById);
}
