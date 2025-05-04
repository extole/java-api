package com.extole.api.person;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Authorization {

    String getAccessToken();

    String[] getScopes();

    String getClientId();

    String getPersonId();
}
