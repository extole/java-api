package com.extole.consumer.rest.impl.barcode;

import java.util.Map;

import javax.ws.rs.ext.ParamConverter;

import com.google.common.collect.ImmutableMap;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.server.internal.LocalizationMessages;

import com.extole.consumer.rest.barcode.BarcodeType;

public final class BarcodeTypeConverter implements ParamConverter<BarcodeType> {

    private static final Map<String, BarcodeType> LEGACY_VALUES = ImmutableMap.of("code128a", BarcodeType.CODE_128);

    @Override
    public BarcodeType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
        }
        BarcodeType barcodeType = LEGACY_VALUES.get(value);
        if (barcodeType == null) {
            try {
                barcodeType = BarcodeType.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new ExtractorException(e);
            }
        }
        return barcodeType;
    }

    @Override
    public String toString(BarcodeType type) {
        if (type == null) {
            throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
        }
        return type.name();
    }
}
