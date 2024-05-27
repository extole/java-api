package com.extole.api.step.action;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;
import com.extole.api.PersonContext;

@Schema
public interface AsyncActionContext extends GlobalContext, PersonContext, LoggerContext {
}
