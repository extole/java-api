package com.extole.common.lang;

@FunctionalInterface
public interface ThrowingConsumer<INPUT, EXCEPTION extends Exception> {

    void accept(INPUT type) throws EXCEPTION;

}
