package com.extole.api.impl.model;

import com.extole.api.model.UserProperty;
import com.extole.common.lang.ToString;
import com.extole.event.model.change.property.UserPropertyPojo;

final class UserPropertyImpl implements UserProperty {
    private final UserPropertyPojo userProperty;

    UserPropertyImpl(UserPropertyPojo userProperty) {
        this.userProperty = userProperty;
    }

    @Override
    public String getId() {
        return userProperty.getId().getValue();
    }

    @Override
    public String getName() {
        return userProperty.getName();
    }

    @Override
    public String getValue() {
        return userProperty.getValue();
    }

    @Override
    public String getUserId() {
        return userProperty.getUserId().getValue();
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }

}
