package com.extole.common.jwt.decode;

import com.extole.common.jwt.JwtException;

public class PayloadClaimParseException extends JwtException {
    public PayloadClaimParseException(String message) {
        super(message);
    }

    public PayloadClaimParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
