package com.extole.common.rest.support.parser;

import static java.util.Objects.isNull;

import javax.annotation.Nullable;

import com.extole.common.rest.exception.QueryLimitsRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;

public final class QueryLimitsParser {

    private QueryLimitsParser() {

    }

    public static int parseLimit(@Nullable String limit, int defaultValue) throws QueryLimitsRestException {
        if (isNull(limit)) {
            return defaultValue;
        }
        try {
            return Integer.parseUnsignedInt(limit);
        } catch (NumberFormatException e) {
            throw RestExceptionBuilder.newBuilder(QueryLimitsRestException.class)
                .withErrorCode(QueryLimitsRestException.INVALID_LIMIT)
                .addParameter("limit", limit)
                .withCause(e)
                .build();
        }
    }

    public static int parseOffset(@Nullable String offset, int defaultValue) throws QueryLimitsRestException {
        if (isNull(offset)) {
            return defaultValue;
        }
        try {
            return Integer.parseUnsignedInt(offset);
        } catch (NumberFormatException e) {
            throw RestExceptionBuilder.newBuilder(QueryLimitsRestException.class)
                .withErrorCode(QueryLimitsRestException.INVALID_OFFSET)
                .addParameter("offset", offset)
                .withCause(e)
                .build();
        }
    }

}
