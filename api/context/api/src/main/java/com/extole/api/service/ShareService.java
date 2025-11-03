package com.extole.api.service;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.person.Share;

@Schema
public interface ShareService {

    @Nullable
    Share getShareByPartnerShareId(String partnerShareId);

    @Nullable
    Share getShare(String shareId);

}
