package com.ppobot.exception;

public class BaseException extends RuntimeException {

    public BaseException(String msg, Throwable t) {
        super(msg, t);
    }

    public BaseException(String msg) {
        super(msg);
    }
}