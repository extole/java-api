package com.extole.api.service;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface ShareService {

    @Nullable
    Share getShareByPartnerShareId(String partnerShareId);

    @Nullable
    Share getShare(String shareId);

    interface Share {
        String getId();

        Map<String, String> getData();

        @Nullable
        String getChannel();
    }
}
