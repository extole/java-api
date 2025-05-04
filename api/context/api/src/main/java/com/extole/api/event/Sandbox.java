package com.extole.api.event;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface Sandbox {

    String getSandbox();

    String getContainer();

}
