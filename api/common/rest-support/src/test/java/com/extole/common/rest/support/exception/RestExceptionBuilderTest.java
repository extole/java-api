package com.extole.common.rest.support.exception;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.extole.common.rest.exception.ErrorCode;
import com.extole.common.rest.exception.ExtoleRestException;
import com.extole.common.rest.exception.RestExceptionBuilder;
import com.extole.common.rest.exception.UnbuildableRestRuntimeException;

public class RestExceptionBuilderTest {

    public static class SampleRestException extends ExtoleRestException {
        public static final ErrorCode<SampleRestException> BAD1 = new ErrorCode<>("bad1", 400, "bad1 message");
        public static final ErrorCode<SampleRestException> BAD2 = new ErrorCode<>("bad2", 400, "bad2 message",
            "parameter1");
        public static final ErrorCode<SampleRestException> BAD3 = new ErrorCode<>("bad3", 500, "bad3 message");

        public SampleRestException(String uniqueId, ErrorCode<SampleRestException> code,
            Map<String, Object> attributes, Throwable cause) {
            super(uniqueId, code, attributes, cause);
        }
    }

    @Test
    public void testBuildSampleException() throws Exception {
        SampleRestException exception = RestExceptionBuilder.newBuilder(SampleRestException.class)
            .withErrorCode(SampleRestException.BAD1)
            .build();

        assertTrue(exception != null);
    }

    @Test
    public void testBuildSampleExceptionValid() throws Exception {
        RestExceptionBuilder<SampleRestException> builder = RestExceptionBuilder.newBuilder(SampleRestException.class);

        SampleRestException exception = builder
            .withErrorCode(SampleRestException.BAD1)
            .build();

        assertTrue(exception != null);
        assertTrue(builder.isValid());
    }

    @Test
    public void testBuildSampleExceptionNoCode() {
        RestExceptionBuilder<SampleRestException> builder = RestExceptionBuilder.newBuilder(SampleRestException.class);

        assertThrows(UnbuildableRestRuntimeException.class, () -> builder.build());
    }

    @Test
    public void testBuildSampleExceptionUnexpectedParameter() throws Exception {
        RestExceptionBuilder<SampleRestException> builder = RestExceptionBuilder.newBuilder(SampleRestException.class);

        SampleRestException exception = builder
            .withErrorCode(SampleRestException.BAD1)
            .addParameter("invalid", "testing123")
            .build();

        assertTrue(exception != null);
        assertFalse(builder.isValid());
    }

    @Test
    public void testBuildSampleExceptionMissingExpectedParameter() throws Exception {
        RestExceptionBuilder<SampleRestException> builder = RestExceptionBuilder.newBuilder(SampleRestException.class);

        SampleRestException exception = builder
            .withErrorCode(SampleRestException.BAD2)
            .build();

        assertTrue(exception != null);
        assertFalse(builder.isValid());
    }
}
