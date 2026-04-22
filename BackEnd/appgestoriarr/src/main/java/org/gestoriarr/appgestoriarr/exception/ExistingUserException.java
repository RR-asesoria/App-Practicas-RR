package org.gestoriarr.appgestoriarr.exception;

public class ExistingUserException extends IllegalStateException {
    public ExistingUserException(String message) {
        super(message);
    }
}
