package com.weproov.app.models.exceptions;

import java.net.HttpURLConnection;

public class LoginException extends NetworkException {

    public LoginException() {
        super();
        init();
        this.errorMessage = "Login failed!";
    }

    public LoginException(String message) {
        super(message);
        init();
        this.errorMessage = message;
    }

    private void init() {
        setStatus(HttpURLConnection.HTTP_UNAUTHORIZED);
    }
}
