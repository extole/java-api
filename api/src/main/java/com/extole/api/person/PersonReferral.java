package com.extole.api.person;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface PersonReferral {

    String getClientId();

    String getOtherPersonId();

    String getMySide();

    String getCreatedDate();

    String getUpdatedDate();

    String getReason();

    boolean isDisplaced();

    @Nullable
    String getContainer();

}
