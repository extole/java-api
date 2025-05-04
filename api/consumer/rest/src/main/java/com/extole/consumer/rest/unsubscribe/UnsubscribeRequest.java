package com.extole.consumer.rest.unsubscribe;

import java.util.Optional;

import javax.ws.rs.QueryParam;

import com.extole.common.lang.ToString;

public class UnsubscribeRequest {

    public static final String LIST_NAME = "list_name";
    private static final String ENCRYPTED_EMAIL = "encrypted_email";
    private static final String SOURCE = "source";
    private final String listName;
    private final String encryptedEmail;
    private final Optional<String> source;

    public UnsubscribeRequest(@QueryParam(LIST_NAME) String listName,
        @QueryParam(ENCRYPTED_EMAIL) String encryptedEmail,
        @QueryParam(SOURCE) Optional<String> source) {
        this.listName = listName;
        this.encryptedEmail = encryptedEmail;
        this.source = source;
    }

    @QueryParam(LIST_NAME)
    public String getListName() {
        return listName;
    }

    @QueryParam(ENCRYPTED_EMAIL)
    public String getEncryptedEmail() {
        return encryptedEmail;
    }

    @QueryParam(SOURCE)
    public Optional<String> getSource() {
        return source;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String listName;
        private String encryptedEmail;
        private String source;

        public Builder withListName(String listName) {
            this.listName = listName;
            return this;
        }

        public Builder withEncryptedEmail(String encryptedEmail) {
            this.encryptedEmail = encryptedEmail;
            return this;
        }

        public Builder withSource(String source) {
            this.source = source;
            return this;
        }

        public UnsubscribeRequest build() {
            return new UnsubscribeRequest(listName, encryptedEmail, Optional.ofNullable(source));
        }
    }

    @Override
    public String toString() {
        return ToString.create(this);
    }
}
