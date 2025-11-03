package com.extole.common.email;

import java.util.Optional;

public interface Email {

    Optional<String> getTitle();

    String getAddress();

    String getNormalizedAddress();
}
