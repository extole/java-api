package com.extole.reporting.rest.impl.audience.member;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.extole.reporting.rest.audience.member.AudienceMemberResponse;

public class AudienceMemberJsonWriter implements AudienceMemberWriter {

    private boolean isFirstBatch = true;

    @Override
    public void writeFirstLine(OutputStream outputStream) throws IOException {
        outputStream.write("[\n".getBytes());
    }

    @Override
    public void write(List<AudienceMemberResponse> members, OutputStream outputStream)
        throws IOException {
        for (int i = 0; i < members.size(); i++) {
            if (!(isFirstBatch && i == 0)) {
                outputStream.write(",\n".getBytes());
            } else {
                outputStream.write("\n".getBytes());
            }
            byte[] json = OBJECT_MAPPER.writeValueAsBytes(members.get(i));
            outputStream.write(json);
        }
        isFirstBatch = false;
    }

    @Override
    public void writeLastLine(OutputStream outputStream) throws IOException {
        outputStream.write("]".getBytes());
    }

}
