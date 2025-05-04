package com.extole.reporting.rest.impl.audience.member;

import org.springframework.stereotype.Component;

@Component
public class AudienceMemberWriterFactory {

    public AudienceMemberWriter getWriter(AudienceMemberDownloadFormat format) {
        switch (format) {
            case JSON:
                return new AudienceMemberJsonWriter();
            case CSV:
                return new AudienceMemberCsvWriter();
            case XLSX:
                return new AudienceMemberXlsxWriter();
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

}
