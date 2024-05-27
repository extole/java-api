package com.extole.api.model.campaign;

import java.util.Map;

public interface QualityRule {

    String getId();

    String getRuleType();

    boolean getEnabled();

    String getCreatedDate();

    String getUpdatedDate();

    Map<String, String[]> getProperties();

    String[] getActionTypes();

}
