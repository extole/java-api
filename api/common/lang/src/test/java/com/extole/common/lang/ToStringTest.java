package com.extole.common.lang;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"checkstyle:StaticVariableName", "checkstyle:VisibilityModifier"})
public class ToStringTest {

    @Test
    public void testToString() throws Exception {
        String field1 = "value1";
        boolean field2 = true;
        Integer field3 = Integer.valueOf(3);
        Long field4 = Long.valueOf(4);
        Instant field5 = Instant.now();
        boolean field6 = true;

        SomeObject object = new SomeObject(field1, field2, field3, field4, field5, field6);
        String string = object.toString();
        Map jsonFields = new ObjectMapper().readValue(string, Map.class);

        assertThat(jsonFields.size()).isEqualTo(6);
        assertThat(jsonFields.get("field1")).isEqualTo(field1);
        assertThat(jsonFields.get("field2")).isEqualTo(field2);
        assertThat(jsonFields.get("field3")).isEqualTo(field3);
        assertThat(jsonFields.get("field4")).isEqualTo(field4.intValue());
        assertThat(jsonFields.get("field5")).isEqualTo(field5.truncatedTo(ChronoUnit.MILLIS).toString());
        assertThat(jsonFields.get("field6")).isEqualTo(field6);
    }

    private static class SomeObject {
        private static final String STATIC_FIELD1 = "value1";
        static final String STATIC_FIELD2 = "value2";
        protected static final String STATIC_FIELD3 = "value3";
        public static final String STATIC_FIELD4 = "value4";
        public static final boolean STATIC_FIELD5 = true;

        private static void setStaticField1(String staticField1) {
            throw new UnsupportedOperationException("Should not be used");
        }

        private static String getStaticField1() {
            throw new UnsupportedOperationException("Should not be used");
        }

        static void setStaticField2(String staticField2) {
            throw new UnsupportedOperationException("Should not be used");
        }

        static String getStaticField2() {
            throw new UnsupportedOperationException("Should not be used");
        }

        protected static void setStaticField3(String staticField3) {
            throw new UnsupportedOperationException("Should not be used");
        }

        protected static String getStaticField3() {
            throw new UnsupportedOperationException("Should not be used");
        }

        public static void setStaticField4(String staticField4) {
            throw new UnsupportedOperationException("Should not be used");
        }

        public static String getStaticField4() {
            throw new UnsupportedOperationException("Should not be used");
        }

        public static void setStaticField5(boolean staticField4) {
            throw new UnsupportedOperationException("Should not be used");
        }

        public static String isStaticField5() {
            throw new UnsupportedOperationException("Should not be used");
        }

        private final String field1;
        private final boolean field2;
        Integer field3;
        protected Long field4;
        public Instant field5;
        public boolean field6;

        SomeObject(String field1, boolean field2, Integer field3, Long field4, Instant field5, boolean field6) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
            this.field4 = field4;
            this.field5 = field5;
            this.field6 = field6;
        }

        private String getField1() {
            throw new UnsupportedOperationException("Should not be used");
        }

        private void setField1(String field1) {
            throw new UnsupportedOperationException("Should not be used");
        }

        private boolean isField2() {
            throw new UnsupportedOperationException("Should not be used");
        }

        private void setField2(boolean field2) {
            throw new UnsupportedOperationException("Should not be used");
        }

        Integer getField3() {
            throw new UnsupportedOperationException("Should not be used");
        }

        void setField3(Integer field3) {
            throw new UnsupportedOperationException("Should not be used");
        }

        protected Long getField4() {
            throw new UnsupportedOperationException("Should not be used");
        }

        protected void setField4(Long field4) {
            throw new UnsupportedOperationException("Should not be used");
        }

        public Instant getField5() {
            throw new UnsupportedOperationException("Should not be used");
        }

        public void setField5(Instant field5) {
            throw new UnsupportedOperationException("Should not be used");
        }

        public boolean isField6() {
            throw new UnsupportedOperationException("Should not be used");
        }

        public boolean getField6() {
            throw new UnsupportedOperationException("Should not be used");
        }

        public void setField6(boolean field6) {
            throw new UnsupportedOperationException("Should not be used");
        }

        @Override
        public String toString() {
            return ToString.create(this);
        }
    }
}
