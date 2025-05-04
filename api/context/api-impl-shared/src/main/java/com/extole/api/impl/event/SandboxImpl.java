package com.extole.api.impl.event;

import com.extole.api.event.Sandbox;
import com.extole.sandbox.SandboxModel;

public class SandboxImpl implements Sandbox {

    private final String sandbox;
    private final String container;

    public SandboxImpl(String sandbox, String container) {
        this.sandbox = sandbox;
        this.container = container;
    }

    public SandboxImpl(SandboxModel sandboxModel) {
        this.sandbox = sandboxModel.getId().getValue();
        this.container = sandboxModel.getContainer().getName();
    }

    public SandboxImpl(com.extole.sandbox.Sandbox sandbox) {
        this.sandbox = sandbox.getSandboxId();
        this.container = sandbox.getContainer().getName();
    }

    @Override
    public String getSandbox() {
        return sandbox;
    }

    @Override
    public String getContainer() {
        return container;
    }

    @Override
    public String toString() {
        return sandbox;
    }

}
