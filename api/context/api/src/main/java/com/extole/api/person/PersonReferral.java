package com.extole.api.person;

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

    String getContainer();

}
