package com.extole.reporting.rest.impl.audience.member;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.extole.reporting.rest.audience.member.AudienceMemberResponse;

public class AudienceMemberXlsxWriter implements AudienceMemberWriter {

    private static final int MAX_SHEET_CAPACITY = 1000000;

    private final SXSSFWorkbook workbook;
    private Sheet currentSheet;
    private AtomicInteger rowCount;

    public AudienceMemberXlsxWriter() {
        this.workbook = new SXSSFWorkbook();
        this.currentSheet = workbook.createSheet();
        this.rowCount = new AtomicInteger(1);
    }

    @Override
    public void writeFirstLine(OutputStream outputStream) {
        Row headerRow = currentSheet.createRow(0);
        int column = 0;
        for (String header : HEADERS) {
            Cell cell = headerRow.createCell(column++);
            cell.setCellValue(header);
        }
    }

    @Override
    public void write(List<AudienceMemberResponse> members, OutputStream outputStream) {
        for (AudienceMemberResponse response : members) {
            if (rowCount.get() > MAX_SHEET_CAPACITY) {
                currentSheet = workbook.createSheet();
                writeFirstLine(outputStream);
                rowCount = new AtomicInteger(1);
            }
            Row row = currentSheet.createRow(rowCount.getAndIncrement());
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(response.getId());
            row.createCell(cellIndex++).setCellValue(response.getIdentityKey());
            row.createCell(cellIndex++).setCellValue(response.getIdentityKeyValue().orElse(null));
            row.createCell(cellIndex++).setCellValue(response.getEmail());
            row.createCell(cellIndex++).setCellValue(response.getFirstName());
            row.createCell(cellIndex++).setCellValue(response.getLastName());
            row.createCell(cellIndex++).setCellValue(response.getPictureUrl());
            row.createCell(cellIndex++).setCellValue(response.getPartnerUserId());
            row.createCell(cellIndex++).setCellValue(response.getLocale().getUserSpecified());
            row.createCell(cellIndex++).setCellValue(response.getLocale().getLastBrowser());
            row.createCell(cellIndex++).setCellValue(response.getVersion());
            row.createCell(cellIndex).setCellValue(String.valueOf(response.isBlocked()));
        }
    }

    @Override
    public void writeLastLine(OutputStream outputStream) throws IOException {
        workbook.write(outputStream);
        workbook.dispose();
        workbook.close();
    }

}
