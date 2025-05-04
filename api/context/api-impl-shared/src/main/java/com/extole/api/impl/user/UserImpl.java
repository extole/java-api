package com.extole.api.impl.user;

import java.util.Optional;

import com.extole.api.user.User;
import com.extole.common.lang.ToString;

public class UserImpl implements User {

    private final String clientId;
    private final String id;
    private final String email;
    private final Optional<String> firstName;
    private final Optional<String> lastName;

    public UserImpl(String clientId,
        String id,
        String email,
        Optional<String> firstName,
        Optional<String> lastName) {
        this.clientId = clientId;
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserImpl(com.extole.model.entity.user.User user) {
        this.clientId = user.getClientId().getValue();
        this.id = user.getId().getValue();
        this.email = user.getNormalizedEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getFirstName() {
        return firstName.orElse(null);
    }

    @Override
    public String getLastName() {
        return lastName.orElse(null);
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
