package com.extole.common.rest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import javax.ws.rs.core.Response.Status.Family;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public class HttpResponseReader<T> {
    private static final Predicate<StatusLine> DEFAULT_SUCCESS_PREDICATE =
        statusLine -> Family.familyOf(statusLine.getStatusCode()) == Family.SUCCESSFUL;

    private final HttpResponse httpResponse;
    private final HttpResponseErrorHandler defaultErrorHandler;

    private Predicate<StatusLine> successPredicate = DEFAULT_SUCCESS_PREDICATE;
    private HttpResponseSuccessHandler<T> successHandler;
    private HttpResponseErrorHandler errorHandler;

    public HttpResponseReader(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
        this.defaultErrorHandler = (statusLine,
            inputStream) -> new HttpResponseReaderException("Non-success response: " + httpResponse.toString(),
                statusLine);
        this.errorHandler = defaultErrorHandler;
    }

    /** optional, if not provided then only the response status will be checked */
    public HttpResponseReader<T> withSuccessHandler(HttpResponseSuccessHandler<T> successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    /** optional, by default it checks if the family of the response code is SUCCESSFUL (2XX) */
    public HttpResponseReader<T> withSuccessPredicate(Predicate<StatusLine> successPredicate) {
        this.successPredicate = successPredicate != null ? successPredicate : DEFAULT_SUCCESS_PREDICATE;
        return this;
    }

    /** optional, by default it will throw an exception with the response status line information */
    public HttpResponseReader<T> withErrorHandler(HttpResponseErrorHandler errorHandler) {
        this.errorHandler = errorHandler != null ? errorHandler : defaultErrorHandler;
        return this;
    }

    /** return the result from successHandler or null in case the successHandler was not defined */
    public T read() throws HttpResponseReaderException {
        StatusLine statusLine = httpResponse.getStatusLine();

        if (!successPredicate.test(statusLine)) {
            if (httpResponse.getEntity() != null) {
                try {
                    throw errorHandler.onError(statusLine, httpResponse.getEntity().getContent());
                } catch (IOException e) {
                    throw new HttpResponseReaderException(
                        "Unable to read the non-success response: " + httpResponse.toString(), statusLine, e);
                }
            }
            throw new HttpResponseReaderException("Non-success response: " + httpResponse.toString(), statusLine);
        }

        if (successHandler == null) {
            return null;
        }

        if (httpResponse.getEntity() == null) {
            throw new HttpResponseReaderException("Empty response: " + httpResponse.toString(), statusLine);
        }
        try {
            return successHandler.onSuccess(statusLine, httpResponse.getEntity().getContent());
        } catch (IOException e) {
            throw new HttpResponseReaderException("Unable to read the response: " + httpResponse.toString(), statusLine,
                e);
        }
    }

    public interface HttpResponseErrorHandler {
        HttpResponseReaderException onError(StatusLine statusLine, InputStream inputStream) throws IOException;
    }

    public interface HttpResponseSuccessHandler<T> {
        T onSuccess(StatusLine statusLine, InputStream inputStream) throws IOException;
    }

    public static class HttpResponseReaderException extends Exception {
        private final StatusLine statusLine;

        public HttpResponseReaderException(String message, StatusLine statusLine) {
            super(message);
            this.statusLine = statusLine;
        }

        public HttpResponseReaderException(String message, StatusLine statusLine, Throwable cause) {
            super(message, cause);
            this.statusLine = statusLine;
        }

        public StatusLine getStatusLine() {
            return statusLine;
        }
    }
}
