package com.extole.api.campaign;

import javax.annotation.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import com.extole.api.person.Person;
import com.extole.api.service.LocaleParseService;

@Schema
public interface VariantSelectionContext {

    String selectVariant(@Nullable String[] preferredVariants);

    LocaleParseService getLocaleParseService();

    Person getPerson();
}
