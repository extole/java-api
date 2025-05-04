package com.extole.api.impl.batch.column;

import java.util.Optional;
import java.util.regex.Pattern;

import com.extole.api.batch.column.BatchJobColumn;
import com.extole.api.batch.column.FullNameMatchBatchJobColumn;
import com.extole.reporting.entity.batch.column.BatchJobColumnValidationPolicy;
import com.extole.reporting.entity.batch.column.FullNameMatchBatchJobColumnPojo;
import com.extole.reporting.entity.batch.column.PatternNameMatchBatchJobColumnPojo;

public class BatchJobColumnMapper {

    public BatchJobColumn mapToApi(com.extole.reporting.entity.batch.column.BatchJobColumn column) {
        switch (column.getType()) {
            case FULL_NAME_MATCH:
                return new FullNameMatchBatchJobColumnImpl(
                    (com.extole.reporting.entity.batch.column.FullNameMatchBatchJobColumn) column);
            case PATTERN_NAME_MATCH:
                return new PatternNameMatchBatchJobColumnImpl(
                    (com.extole.reporting.entity.batch.column.PatternNameMatchBatchJobColumn) column);
            default:
                throw new IllegalArgumentException("Unknown BatchJobColumnType: " + column.getType());
        }
    }

    public com.extole.reporting.entity.batch.column.BatchJobColumnPojo mapToEntity(BatchJobColumn column) {
        switch (column.getType()) {
            case FULL_NAME_MATCH:
                com.extole.api.batch.column.FullNameMatchBatchJobColumn fullNameMatchBatchJobColumn =
                    (FullNameMatchBatchJobColumn) column;
                return new FullNameMatchBatchJobColumnPojo(
                    BatchJobColumnValidationPolicy.valueOf(fullNameMatchBatchJobColumn.getValidationPolicy().name()),
                    Optional.ofNullable(fullNameMatchBatchJobColumn.getPrefix()),
                    fullNameMatchBatchJobColumn.getName());
            case PATTERN_NAME_MATCH:
                com.extole.api.batch.column.PatternNameMatchBatchJobColumn patternNameMatchBatchJobColumn =
                    (com.extole.api.batch.column.PatternNameMatchBatchJobColumn) column;
                return new PatternNameMatchBatchJobColumnPojo(
                    BatchJobColumnValidationPolicy.valueOf(patternNameMatchBatchJobColumn.getValidationPolicy().name()),
                    Optional.ofNullable(patternNameMatchBatchJobColumn.getPrefix()),
                    Pattern.compile(patternNameMatchBatchJobColumn.getNamePattern()));
            default:
                throw new IllegalArgumentException("Unknown BatchJobColumnType: " + column.getType());
        }
    }

}
