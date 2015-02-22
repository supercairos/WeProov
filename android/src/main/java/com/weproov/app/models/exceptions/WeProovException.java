package com.weproov.app.models.exceptions;

public abstract class WeProovException extends Exception {

    public WeProovException() {
        super();
    }

    public WeProovException(String message) {
        super(message);
    }
}
