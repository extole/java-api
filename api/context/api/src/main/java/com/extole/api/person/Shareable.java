package com.extole.api.person;

import java.util.Map;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Shareable {

    @Deprecated // TODO use code instead, to be removed in ENG-16408
    String getId();

    String getKey();

    String getCode();

    ShareableContent getContent();

    Map<String, String> getData();

    @Nullable
    String getLabel();

    String getClientDomainId();

    String getPersonId();
}
