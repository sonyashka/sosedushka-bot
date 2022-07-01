package com.ppobot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class NotConnectedException extends BaseException {
    private final static String MESSAGE = "Not Connected";

    public NotConnectedException(Throwable t) {
        super(MESSAGE, t);
    }

    public NotConnectedException() {super(MESSAGE);}
}
