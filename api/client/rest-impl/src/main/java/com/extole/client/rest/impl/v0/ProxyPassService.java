package com.extole.client.rest.impl.v0;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyPassService {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyPassService.class);

    public Response forwardToServer(String target, HttpServletRequest httpRequest)
        throws IOException {
        HttpURLConnection targetUrlConnection = getHttpConnection(target, httpRequest);
        targetUrlConnection.setDoOutput(true);

        passOriginalHeaders(httpRequest, targetUrlConnection);
        passOriginalContent(httpRequest, targetUrlConnection);
        targetUrlConnection.connect();
        int responseCode = targetUrlConnection.getResponseCode();
        ResponseBuilder responseBuilder = passTargetContent(targetUrlConnection, responseCode);
        passTargetHeaders(responseBuilder, targetUrlConnection);

        return responseBuilder.build();
    }

    private HttpURLConnection getHttpConnection(String target, HttpServletRequest httpRequest) throws IOException {
        String queryString = httpRequest.getQueryString();
        if (queryString != null) {
            target += "?" + queryString;
        }
        URL targetUrl = new URL(target);
        return (HttpURLConnection) targetUrl.openConnection();
    }

    private void passOriginalHeaders(HttpServletRequest httpRequest, HttpURLConnection targetUrlConnection) {
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            targetUrlConnection.setRequestProperty(headerName, httpRequest.getHeader(headerName));
        }
    }

    private void passOriginalContent(HttpServletRequest httpRequest, HttpURLConnection targetUrlConnection)
        throws IOException {
        targetUrlConnection.setRequestMethod(httpRequest.getMethod());
        String inputPayload = readContent(httpRequest.getInputStream());
        // calling getOutputStream will automatically change GET to POST so we use a supplier
        writeContent(inputPayload, () -> targetUrlConnection.getOutputStream());
    }

    private void passTargetHeaders(ResponseBuilder responseBuilder, HttpURLConnection targetUrlConnection) {
        for (Map.Entry<String, List<String>> headers : targetUrlConnection.getHeaderFields().entrySet()) {
            String headerName = headers.getKey();
            if (headerName != null && !headerName.startsWith("Access-Control-Allow") &&
                !headerName.startsWith("Transfer-Encoding")) {
                headers.getValue().stream().forEach(value -> responseBuilder.header(headerName, value));
            }
        }
    }

    private ResponseBuilder passTargetContent(HttpURLConnection targetUrlConnection, int responseCode)
        throws IOException {
        String targetResponse = "";
        try {
            targetResponse = readContent(targetUrlConnection.getInputStream());
        } catch (IOException e) {
            if (targetUrlConnection.getErrorStream() != null) {
                targetResponse = readContent(targetUrlConnection.getErrorStream());
            } else {
                LOG.warn(
                    "error stream has no errors or the connection is not connected or the server sent no useful data.",
                    e);
            }
        }

        ResponseBuilder responseBuilder = Response.status(responseCode);
        if (!targetResponse.isEmpty()) {
            responseBuilder.entity(targetResponse);
        }

        return responseBuilder;
    }

    private String readContent(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String inputLine;
            while ((inputLine = inputReader.readLine()) != null) {
                content.append(inputLine);
            }
        }
        return content.toString();
    }

    private void writeContent(String content, StreamSupplier outputStream) throws IOException {
        if (!content.isEmpty()) {
            try (DataOutputStream targetBody = new DataOutputStream(outputStream.get())) {
                targetBody.writeBytes(content);
                targetBody.flush();
            }
        }
    }

    @FunctionalInterface
    private interface StreamSupplier {
        OutputStream get() throws IOException;
    }
}
