package com.extole.reporting.rest.impl.audience.member;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.extole.common.lang.ObjectMapperProvider;
import com.extole.reporting.rest.audience.member.AudienceMemberResponse;

public interface AudienceMemberWriter {

    ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.getConfiguredInstance();

    String ID = "id";
    String IDENTITY_KEY = "identity_key";
    String IDENTITY_KEY_VALUE = "identity_key_value";
    String EMAIL = "email";
    String FIRST_NAME = "first_name";
    String LAST_NAME = "last_name";
    String PICTURE_URL = "picture_url";
    String PARTNER_USER_ID = "partner_user_id";
    String LOCALE_USER_SPECIFIED = "locale.user_specified";
    String LOCALE_LAST_BROWSER = "locale.last_browser";
    String VERSION = "version";
    String BLOCKED = "blocked";

    List<String> HEADERS = List.of(ID, IDENTITY_KEY, IDENTITY_KEY_VALUE, EMAIL, FIRST_NAME, LAST_NAME, PICTURE_URL,
        PARTNER_USER_ID, LOCALE_USER_SPECIFIED, LOCALE_LAST_BROWSER, VERSION, BLOCKED);

    void writeFirstLine(OutputStream outputStream) throws IOException;

    void write(List<AudienceMemberResponse> members, OutputStream outputStream) throws IOException;

    void writeLastLine(OutputStream outputStream) throws IOException;

}
