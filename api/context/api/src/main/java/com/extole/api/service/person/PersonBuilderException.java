package com.extole.api.service.person;

public class PersonBuilderException extends Exception {

    public PersonBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersonBuilderException(Exception cause) {
        super(cause);
    }

    public PersonBuilderException(String message) {
        super(message);
    }

}
