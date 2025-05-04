package com.extole.client.rest.impl.reward.supplier.v2.manual.coupon;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.extole.client.rest.reward.supplier.v2.ManualCouponRequest;
import com.extole.common.lang.date.DateTimeBuilder;
import com.extole.common.lang.date.DateTimeBuilderValidationException;

@Component
class TextManualCouponFileParser implements ManualCouponFileParser {
    private static final String CSV_SEPARATOR = ",";
    private static final int COUPON_CODE_COLUMN_INDEX = 0;
    private static final int EXPIRES_DATE_COLUMN_INDEX = 1;
    private static final int MAX_COLUMNS_COUNT = 2;

    @Override
    public List<ManualCouponRequest> parse(InputStream inputStream, ZoneId timeZone)
        throws ManualCouponFileParsingException {
        List<ManualCouponRequest> couponRequests = new ArrayList<>();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> csvLines = bufferedReader.lines().collect(Collectors.toList());

        for (int i = 0; i < csvLines.size(); i++) {
            ManualCouponRequest couponRequest = parseLine(csvLines.get(i), i, timeZone);
            if (StringUtils.isNotBlank(couponRequest.getCouponCode())) {
                couponRequests.add(couponRequest);
            }
        }
        return couponRequests;
    }

    private ManualCouponRequest parseLine(String csvLine, int lineIx, ZoneId timeZone)
        throws ManualCouponInvalidFileLineException {
        List<String> columnValues = Arrays.asList(csvLine.split(CSV_SEPARATOR));

        validateColumnsCount(columnValues, lineIx);
        try {
            return createCouponRequest(columnValues, timeZone);
        } catch (DateTimeBuilderValidationException e) {
            throw new ManualCouponInvalidExpirationDateFormatException("Could not parse date", lineIx);
        }
    }

    private void validateColumnsCount(List<String> columnValues, int lineIndex)
        throws ManualCouponInvalidFileLineException {
        int columnsCount = columnValues.size();

        if (columnsCount == 0 || columnsCount > MAX_COLUMNS_COUNT) {
            throw new ManualCouponInvalidFileLineException(
                String.format("Invalid number of columns. Actual: %s, Expected: %s  ", Integer.valueOf(columnsCount),
                    Integer.valueOf(MAX_COLUMNS_COUNT)),
                lineIndex + 1);
        }
    }

    private ManualCouponRequest createCouponRequest(List<String> columns, ZoneId timeZone)
        throws DateTimeBuilderValidationException {
        ManualCouponRequest.Builder builder = ManualCouponRequest
            .builder()
            .withCouponCode(columns.get(COUPON_CODE_COLUMN_INDEX).trim());

        if (columns.size() == MAX_COLUMNS_COUNT && StringUtils.isNoneBlank(columns.get(EXPIRES_DATE_COLUMN_INDEX))) {
            builder.withExpiresAt(new DateTimeBuilder().withDateString(columns.get(EXPIRES_DATE_COLUMN_INDEX).trim())
                .withDefaultTimezone(timeZone).build());
        }

        return builder.build();
    }

    @Override
    public ManualCouponFormat getManualCouponFormat() {
        return ManualCouponFormat.TEXT;
    }
}
