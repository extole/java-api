package com.extole.common.lang;

import com.extole.common.lang.exception.NotifiableException;

@NotifiableException(notifiableRuntimeCauses = "java.lang.NumberFormatException")
public class CurrencyNumberParseException extends Exception {
    public CurrencyNumberParseException(Throwable cause) {
        super(cause);
    }
}
