package com.extole.api.service;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface StringService {

    String[] split(@Nullable String value);

    String stripAccents(String value);

    String removeEnd(String str, String remove);

}
