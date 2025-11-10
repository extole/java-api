package com.extole.common.rest.omissible;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class OmissibleModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addBeanSerializerModifier(new OmissibleBeanSerializerModifier());
    }

}
