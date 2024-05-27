package com.extole.api.event;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface InputEventLabel {

    String getName();

    boolean isRequired();
}
