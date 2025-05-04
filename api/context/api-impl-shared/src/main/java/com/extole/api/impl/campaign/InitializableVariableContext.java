package com.extole.api.impl.campaign;

import com.extole.api.campaign.VariableContext;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;

public interface InitializableVariableContext extends VariableContext {

    VariableContext initialize(Id<ClientHandle> clientId, VariableContext target);

}
