package com.extole.reporting.rest.impl.audience.member;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import com.extole.reporting.rest.audience.member.AudienceMemberResponse;

public class AudienceMemberCsvWriter implements AudienceMemberWriter {

    @Override
    public void writeFirstLine(OutputStream outputStream) throws IOException {
        outputStream.write(String.join(",", HEADERS).getBytes());
        outputStream.write("\n".getBytes());
    }

    @Override
    public void write(List<AudienceMemberResponse> members, OutputStream outputStream)
        throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        List<Map<String, String>> csvMembers = mapToMapStringString(members);
        for (Map<String, String> member : csvMembers) {
            JsonNode tree = OBJECT_MAPPER.readTree(OBJECT_MAPPER.writeValueAsString(member));
            CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder().setUseHeader(false);
            for (String header : HEADERS) {
                csvSchemaBuilder.addColumn(header);
            }
            byte[] csvBytes = csvMapper.writer(csvSchemaBuilder.build()).writeValueAsString(tree).getBytes();
            outputStream.write(csvBytes);
        }
    }

    @Override
    public void writeLastLine(OutputStream outputStream) {
    }

    private List<Map<String, String>> mapToMapStringString(List<AudienceMemberResponse> responses) {
        List<Map<String, String>> members = new ArrayList<>();
        responses.forEach(response -> {
            Map<String, String> memberMap = new LinkedHashMap<>();
            memberMap.put(ID, response.getId());
            memberMap.put(IDENTITY_KEY, response.getIdentityKey());
            memberMap.put(IDENTITY_KEY_VALUE, response.getIdentityKeyValue().orElse(null));
            memberMap.put(EMAIL, response.getEmail());
            memberMap.put(FIRST_NAME, response.getFirstName());
            memberMap.put(LAST_NAME, response.getLastName());
            memberMap.put(PICTURE_URL, response.getPictureUrl());
            memberMap.put(PARTNER_USER_ID, response.getPartnerUserId());
            memberMap.put(LOCALE_USER_SPECIFIED, response.getLocale().getUserSpecified());
            memberMap.put(LOCALE_LAST_BROWSER, response.getLocale().getLastBrowser());
            memberMap.put(VERSION, response.getVersion());
            memberMap.put(BLOCKED, String.valueOf(response.isBlocked()));
            members.add(memberMap);
        });

        return members;
    }

}
