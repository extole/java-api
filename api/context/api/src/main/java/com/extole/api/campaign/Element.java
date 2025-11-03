package com.extole.api.campaign;

import java.util.List;

public interface Element {

    enum ElementType {
        CLIENT_KEY, WEBHOOK, REWARD_SUPPLIER, AUDIENCE, CLIENT_DOMAIN_PATTERN
    }

    String getId();

    List<String> getTags();

    String getType();
}
