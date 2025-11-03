package com.extole.common.javascript;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

public class ReportPostHandlersDataFormatContext {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Object[] getReport() throws IOException {
        String reportContent = IOUtils
            .toString(this.getClass().getResourceAsStream("/report-posthandlers/report-summary_per_campaign.json"),
                Charset.defaultCharset());

        return MAPPER.readValue(reportContent, new TypeReference<List<Map<?, ?>>>() {}).toArray();
    }

}
