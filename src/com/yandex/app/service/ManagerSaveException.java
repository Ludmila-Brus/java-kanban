package com.yandex.app.service;

public class ManagerSaveException extends RuntimeException {

    private String strVal;

    public ManagerSaveException(final String message, final String strVal) {
        super(message);
        this.strVal = strVal;
    }

    public String getDetailMessage(){
        return super.getMessage() + " " + this.strVal;
    }
}
