package com.extole.api.person;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Share {
    String getId();

    Map<String, String> getData();

    @Nullable
    String getChannel();
}
