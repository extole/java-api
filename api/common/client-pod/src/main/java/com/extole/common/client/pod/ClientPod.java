package com.extole.common.client.pod;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;

public final class ClientPod {
    public static final ClientPod UNDEFINED = new ClientPod("undefined");
    public static final ClientPod DEFAULT = new ClientPod("default");

    private final String name;

    @JsonCreator
    public ClientPod(String name) {
        Preconditions.checkNotNull(name, "Client pod name cannot be null");
        this.name = name.toLowerCase();
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        ClientPod other = (ClientPod) object;
        return name.equals(other.name);
    }
}
