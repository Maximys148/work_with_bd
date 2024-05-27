package com.hronous.exceptions;

public class DBConnectionException extends RuntimeException {

    public DBConnectionException(String message) {
        super(message);
    }
}
