package com.extole.api.service;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.person.Shareable;

@Schema
public interface ShareableService {

    @Nullable
    Shareable getByCode(String code);
}
