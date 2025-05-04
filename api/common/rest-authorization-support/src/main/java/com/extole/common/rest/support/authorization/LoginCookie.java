package com.extole.common.rest.support.authorization;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.crypto.Mac;

import org.apache.commons.codec.binary.Base64;

import com.extole.authorization.service.AuthorizationException;
import com.extole.authorization.service.ClientHandle;
import com.extole.id.Id;

public class LoginCookie {

    public static final String ENCODING = "UTF-8";

    private static final Pattern SIGNATURE_PATTERN = Pattern.compile("\\&s\\=");
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("\\&t\\=");
    private static final Pattern AMPERSAND_PATTERN = Pattern.compile("\\&");
    private static final Pattern EQUAL_SIGN_PATTERN = Pattern.compile("\\=");
    private static final Pattern PERIOD_PATTERN = Pattern.compile("\\.");

    private final Map<String, String> params = new LinkedHashMap<>();
    private long userId;
    private Id<ClientHandle> clientId;
    private int[] roleIds;
    private int timestamp;
    private String signature;
    private String unsignedValue;

    public LoginCookie(String cookieValue) throws AuthorizationException {
        parseCookie(cookieValue);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Id<ClientHandle> getClientId() {
        return clientId;
    }

    public boolean verify(Mac mac) {
        if (timestamp <= System.currentTimeMillis() / TimeUnit.SECONDS.toMillis(1L)) {
            return false;
        }
        String token = createHashedToken(mac, unsignedValue.toString());
        return token.equals(signature);
    }

    private void parseCookie(String value) throws AuthorizationException {
        if (value == null) {
            throw new IllegalArgumentException("Attempt to parse a null cookieText.");
        }

        try {
            String[] signatureSplit = SIGNATURE_PATTERN.split(value, 2);
            if (signatureSplit.length != 2) {
                throw new AuthorizationException("Could not find the signature in the cookie value " + value);
            }
            signature = signatureSplit[1];
            unsignedValue = signatureSplit[0];

            String[] timestampSplit = TIMESTAMP_PATTERN.split(signatureSplit[0], 2);
            if (timestampSplit.length != 2) {
                throw new AuthorizationException("Could not find the timestamp in the cookie value " + value);
            }
            timestamp = Integer.parseInt(timestampSplit[1]);
            String text = timestampSplit[0];

            String[] arguments = AMPERSAND_PATTERN.split(text);
            for (String keyValuePair : arguments) {
                if (keyValuePair.length() > 0) {
                    String[] keyValueSplit = EQUAL_SIGN_PATTERN.split(keyValuePair, 2);
                    if (keyValueSplit.length == 1) {
                        params.put(keyValueSplit[0], "");
                    } else {
                        params.put(keyValueSplit[0], keyValueSplit[1]);
                    }
                }
            }

            try {
                userId = Long.parseLong(params.remove("u"));
            } catch (RuntimeException e) {
                throw new AuthorizationException("The login cookie: " + value + " does not have a userId.", e);
            }

            try {
                clientId = Id.valueOf(params.remove("c"));
            } catch (RuntimeException e) {
                throw new AuthorizationException("The login cookie: " + value + " does not have a clientId.", e);
            }

            try {
                String roles = params.remove("r");
                if (roles == null || roles.length() == 0) {
                    roleIds = new int[0];
                } else {
                    String[] roleStrings = PERIOD_PATTERN.split(roles);
                    roleIds = new int[roleStrings.length];
                    int i = 0;
                    for (String role : roleStrings) {
                        roleIds[i++] = Integer.parseInt(role);
                    }
                }
            } catch (RuntimeException e) {
                throw new AuthorizationException("The login cookie " + value
                    + " has an invalid list of roleIds.", e);
            }
        } catch (RuntimeException e) {
            throw new AuthorizationException("Error parsing the cookie value: " + value, e);
        }
    }

    private String createHashedToken(Mac mac, String str) {
        try {
            byte[] hashedBytes = mac.doFinal(str.getBytes(ENCODING));

            String base64String = Base64.encodeBase64String(hashedBytes);

            return base64String;
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }
}
