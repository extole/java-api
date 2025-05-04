package com.extole.omissible.types;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.extole.common.lang.ToString;
import com.extole.common.rest.omissible.Omissible;

public class SimplePojo {
    private static final String JSON_A = "a";
    private static final String JSON_B = "b";
    private static final String JSON_C = "c";
    private final Omissible<List<String>> a;
    private final Omissible<List<String>> b;
    private final Omissible<List<String>> c;

    public SimplePojo(@JsonProperty(JSON_A) Omissible<List<String>> a,
        @JsonProperty(JSON_B) Omissible<List<String>> b,
        @JsonProperty(JSON_C) Omissible<List<String>> c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @JsonProperty(JSON_A)
    public Omissible<List<String>> getA() {
        return a;
    }

    @JsonProperty(JSON_B)
    public Omissible<List<String>> getB() {
        return b;
    }

    @JsonProperty(JSON_C)
    public Omissible<List<String>> getC() {
        return c;
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
