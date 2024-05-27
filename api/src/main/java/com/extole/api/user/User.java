package com.extole.api.user;

import javax.annotation.Nullable;

public interface User {

    String getClientId();

    String getId();

    @Nullable
    String getFirstName();

    @Nullable
    String getLastName();

    String getEmail();

}
