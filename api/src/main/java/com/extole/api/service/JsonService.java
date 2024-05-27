package com.extole.api.service;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface JsonService {

    @Nullable
    Object readJsonPath(Object json, String jsonPath);

    String toJsonString(Object object);

    Object toJsonObject(String jsonString);

}
