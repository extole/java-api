package com.extole.client.rest.impl.reward.supplier.v2.manual.coupon;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EmptyFileException;
import org.apache.poi.UnsupportedFileFormatException;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.extole.client.rest.reward.supplier.v2.ManualCouponRequest;
import com.extole.common.lang.date.DateTimeBuilder;
import com.extole.common.lang.date.DateTimeBuilderValidationException;

@Component
class XlsxManualCouponFileParser implements ManualCouponFileParser {
    private static final int SHEET_INDEX = 0;
    public static final int MAX_COLUMNS_COUNT = 2;
    public static final int COUPON_CODE_COLUMN_INDEX = 0;
    private static final int EXPIRES_DATE_COLUMN_INDEX = 1;

    @Override
    public List<ManualCouponRequest> parse(InputStream inputStream, ZoneId timeZone)
        throws ManualCouponFileParsingException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            return doParse(workbook, timeZone);
        } catch (IOException | POIXMLException | EmptyFileException | UnsupportedFileFormatException e) {
            throw new ManualCouponFileParsingException("Coupon file was corrupted or not properly formatted", e);
        }
    }

    private List<ManualCouponRequest> doParse(XSSFWorkbook workbook, ZoneId timeZone)
        throws ManualCouponFileParsingException {
        List<ManualCouponRequest> couponRequests = new ArrayList<>();
        XSSFSheet sheet = workbook.getSheetAt(SHEET_INDEX);

        for (Row row : sheet) {
            final Optional<ManualCouponRequest> parsedCouponRequest = parseRow(row, timeZone);
            parsedCouponRequest.ifPresent(couponRequest -> couponRequests.add(couponRequest));
        }
        return couponRequests;
    }

    private Optional<ManualCouponRequest> parseRow(Row row, ZoneId timeZone)
        throws ManualCouponFileParsingException {
        validateColumnsCount(row);
        return createCouponRequest(row, timeZone);
    }

    private Optional<ManualCouponRequest> createCouponRequest(Row row, ZoneId timeZone)
        throws ManualCouponFileParsingException {
        final Cell couponCodeCell = row.getCell(COUPON_CODE_COLUMN_INDEX);

        if (isEmptyCell(couponCodeCell)) {
            return Optional.empty();
        }

        final ManualCouponRequest.Builder builder = ManualCouponRequest
            .builder()
            .withCouponCode(getStringCellValue(couponCodeCell));

        if (row.getPhysicalNumberOfCells() == MAX_COLUMNS_COUNT) {
            Optional<ZonedDateTime> expiresDate = getExpiresDateValue(row.getCell(EXPIRES_DATE_COLUMN_INDEX), timeZone);
            expiresDate.ifPresent(date -> builder.withExpiresAt(date));
        }

        return Optional.of(builder.build());
    }

    private boolean isEmptyCell(Cell couponCodeCell) {
        return Objects.isNull(couponCodeCell) || StringUtils.isBlank(getStringCellValue(couponCodeCell));
    }

    private void validateColumnsCount(Row row) throws ManualCouponInvalidFileLineException {
        final int columnsCount = row.getPhysicalNumberOfCells();
        if (columnsCount == 0 || columnsCount > MAX_COLUMNS_COUNT) {
            throw new ManualCouponInvalidFileLineException(
                String.format("Invalid number of columns. Actual: %s, Expected: %s  ", Integer.valueOf(columnsCount),
                    Integer.valueOf(MAX_COLUMNS_COUNT)),
                row.getRowNum() + 1);
        }
    }

    private Optional<ZonedDateTime> getExpiresDateValue(Cell cell, ZoneId timeZone)
        throws ManualCouponInvalidExpirationDateFormatException {
        try {
            String expiresDateString =
                DateUtil.isCellDateFormatted(cell) ? formatDateValue(cell, timeZone) : getStringCellValue(cell);
            ZonedDateTime expiresDate = StringUtils.isBlank(expiresDateString) ? null
                : new DateTimeBuilder().withDateString(expiresDateString).withDefaultTimezone(timeZone).build();
            return Optional.ofNullable(expiresDate);
        } catch (IllegalStateException | DateTimeParseException | DateTimeBuilderValidationException e) {
            throw new ManualCouponInvalidExpirationDateFormatException("Could not parse expiration date",
                cell.getRowIndex() + 1);
        }
    }

    private String formatDateValue(Cell expiresAt, ZoneId timeZone) {
        final Instant expiresAtValue = expiresAt.getDateCellValue().toInstant();

        return ISO_OFFSET_DATE_TIME.withZone(timeZone).format(expiresAtValue);
    }

    private String getStringCellValue(Cell cell) {
        String value = "";
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                value = String.valueOf(cell.getDateCellValue());
            } else {
                value = String.valueOf(cell.getNumericCellValue());
            }
        } else if (cell.getCellType() == CellType.STRING) {
            value = cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.FORMULA) {
            value = String.valueOf(cell.getCellFormula());
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            value = String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.ERROR) {
            value = String.valueOf(cell.getErrorCellValue());
        }
        return value;
    }

    @Override
    public ManualCouponFormat getManualCouponFormat() {
        return ManualCouponFormat.XLSX;
    }
}
