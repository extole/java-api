package com.extole.api.campaign.component.install;

import com.extole.api.campaign.VariableContext;

public interface ComponentInstalltimeContext {

    SourceComponent getSourceComponent();

    TargetComponent getTargetComponent();

    VariableContext getVariableContext();

    ComponentInstalltimeService getComponentService();

    VariableContext getVariableContext(String defaultKey);

    VariableContext getVariableContext(String... defaultKeys);

}
