package com.extole.api.step.action;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.GlobalContext;
import com.extole.api.LoggerContext;
import com.extole.api.PersonContext;
import com.extole.api.event.Sandbox;

@Schema
public interface AsyncActionContext extends GlobalContext, PersonContext, LoggerContext {

    Map<String, Object> getData();

    Sandbox getSandbox();

    String getCampaignId();

    String getProgramLabel();

}
