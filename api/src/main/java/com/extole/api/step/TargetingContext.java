package com.extole.api.step;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.person.PersonReferral;

@Schema
public interface TargetingContext {

    @Nullable
    PersonReferral getReferral();

}
