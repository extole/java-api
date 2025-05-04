package com.extole.api.service;

import javax.annotation.Nullable;

import com.extole.api.user.User;

public interface UserService {

    @Nullable
    User getUserById(String userId);

}
