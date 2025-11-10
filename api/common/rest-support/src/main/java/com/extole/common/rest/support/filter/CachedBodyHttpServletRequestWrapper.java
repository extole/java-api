package com.extole.common.rest.support.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.input.BOMInputStream;
import org.springframework.util.StreamUtils;

public class CachedBodyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] httpRequestBody;

    public CachedBodyHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        httpRequestBody = StreamUtils.copyToByteArray(new BOMInputStream(request.getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(httpRequestBody);
        final AtomicBoolean finished = new AtomicBoolean(false);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return finished.get();
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() {
                int data = byteArrayInputStream.read();
                if (data == -1) {
                    finished.set(true);
                }

                return data;
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(httpRequestBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }

    public byte[] getHttpRequestBody() {
        return Arrays.copyOf(httpRequestBody, httpRequestBody.length);
    }
}
