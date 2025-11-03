package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface JwtService {

    JwtBuilder createJwtBuilder();

    boolean isValid(String jwt);

}
