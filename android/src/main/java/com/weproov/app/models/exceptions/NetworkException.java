package com.weproov.app.models.exceptions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NetworkException extends WeProovException {

    @Expose
    @SerializedName("message")
    protected String errorMessage;
    @Expose
    protected int code;
    private int status;

    public NetworkException() {
        super();
    }

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(int status, int code, String message) {
        this.status = status;
        this.code = code;
        this.errorMessage = message;
    }

    /**
     * @return the HTTP Response code
     */
    public int getStatus() {
        return status;
    }

    public void setStatus(int code) {
        this.status = code;
    }

    @Override
    public String getMessage() {
        return "Server returned code " + code + " and message " + errorMessage + " HTTP status was " + status;
    }
}


