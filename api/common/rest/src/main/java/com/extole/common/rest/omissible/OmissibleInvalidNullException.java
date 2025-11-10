package com.extole.common.rest.omissible;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.exc.InvalidNullException;

public class OmissibleInvalidNullException extends InvalidNullException {

    OmissibleInvalidNullException(DeserializationContext deserializationContext,
        String message,
        PropertyName propertyName) {
        super(deserializationContext, message, propertyName);
    }

}
