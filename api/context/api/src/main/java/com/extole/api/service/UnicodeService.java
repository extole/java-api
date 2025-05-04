package com.extole.api.service;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public interface UnicodeService {

    String nfdNormalized(CharSequence src);

    boolean isNfdNormalized(CharSequence src);

}
