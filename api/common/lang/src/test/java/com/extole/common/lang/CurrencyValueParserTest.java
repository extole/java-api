package com.extole.common.lang;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class CurrencyValueParserTest {

    @Test
    public void testParseInteger() {
        assertThat(CurrencyValueParser.parseValue("10").toString()).isEqualTo("10.00");
    }

    @Test
    public void testParseDollarSign() {
        assertThat(CurrencyValueParser.parseValue("$10.10").toString()).isEqualTo("10.10");
    }

    @Test
    public void testParseDouble() {
        assertThat(CurrencyValueParser.parseValue("10.10").toString()).isEqualTo("10.10");
        assertThat(CurrencyValueParser.parseValue("10000.12").toString()).isEqualTo("10000.12");
    }

    @Test
    public void testParseCommaAsDecimalPoint() {
        assertThat(CurrencyValueParser.parseValue("10,10").toString()).isEqualTo("10.10");
        assertThat(CurrencyValueParser.parseValue("10,12").toString()).isEqualTo("10.12");
        assertThat(CurrencyValueParser.parseValue("10000,12").toString()).isEqualTo("10000.12");
        assertThat(CurrencyValueParser.parseValue("10.000,12").toString()).isEqualTo("10000.12");
    }

    @Test
    public void testParseCommaAsDecimalPointWithoutExactTrailingDigits() {
        assertThat(CurrencyValueParser.parseValue("10,1").toString()).isEqualTo("10.10");
        assertThat(CurrencyValueParser.parseValue("10,").toString()).isEqualTo("10.00");
        assertThat(CurrencyValueParser.parseValue("10000,2").toString()).isEqualTo("10000.20");
        assertThat(CurrencyValueParser.parseValue("10.000,").toString()).isEqualTo("10000.00");
    }

    @Test
    public void testParseCommaAsThousands() {
        assertThat(CurrencyValueParser.parseValue("1,000").toString()).isEqualTo("1000.00");
        assertThat(CurrencyValueParser.parseValue("10,000.12").toString()).isEqualTo("10000.12");
        assertThat(CurrencyValueParser.parseValue("11.000,22").toString()).isEqualTo("11000.22");
    }

    @Test
    public void testParseInvalid() {
        assertThat(CurrencyValueParser.parseValue("test").toString()).isEqualTo("0");
    }

    @Test
    public void testParseEmpty() {
        assertThat(CurrencyValueParser.parseValue("").toString()).isEqualTo("0");
    }
}
