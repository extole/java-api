package com.extole.api;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.service.GlobalServices;

@Schema
public interface GlobalContext {

    ClientContext getClientContext();

    GlobalServices getGlobalServices();

}
