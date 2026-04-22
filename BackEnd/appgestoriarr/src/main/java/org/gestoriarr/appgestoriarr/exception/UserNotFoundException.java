package org.gestoriarr.appgestoriarr.exception;

public class UserNotFoundException extends IllegalArgumentException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
