package com.ppobot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.RESET_CONTENT)
public class DataException extends BaseException {

    private final static String MESSAGE = "Wrong Data";

    public DataException(Throwable t) {
        super(MESSAGE, t);
    }

    public DataException() {super(MESSAGE);}
}
