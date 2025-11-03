package com.extole.common.lang;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.BiFunction;

public final class CurrencyValueParser {

    public static final BiFunction<String, Throwable, BigDecimal> DEFAULT_HANDLER = (value, cause) -> BigDecimal.ZERO;

    private static final int POSSIBLE_DECIMAL_END_LENGTH = 3;

    public static BigDecimal parseValue(String currencyValue) {
        return parseValue(currencyValue, DEFAULT_HANDLER);
    }

    public static BigDecimal parseValue(String currencyValue,
        BiFunction<String, Throwable, BigDecimal> defaultOnExceptionHandler) {
        if (currencyValue == null) {
            return BigDecimal.ZERO;
        }
        currencyValue = currencyValue.trim().replace("$", "");
        if (currencyValue == "") {
            return BigDecimal.ZERO;
        }
        Locale locale = Locale.US;
        if (currencyValue.length() - currencyValue.lastIndexOf(",") <= POSSIBLE_DECIMAL_END_LENGTH) {
            locale = Locale.GERMANY;
        }
        try {
            double doubleValue = DecimalFormat.getNumberInstance(locale).parse(currencyValue).doubleValue();
            BigDecimal result = new BigDecimal(doubleValue);
            return result.setScale(2, RoundingMode.HALF_UP);
        } catch (ParseException | NumberFormatException e) {
            return defaultOnExceptionHandler.apply(currencyValue, new CurrencyNumberParseException(e));
        }
    }

    private CurrencyValueParser() {
    }
}
