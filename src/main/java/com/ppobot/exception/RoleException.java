package com.ppobot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class RoleException extends BaseException {

    private final static String MESSAGE = "Action not permitted";

    public RoleException(Throwable t) {
        super(MESSAGE, t);
    }

    public RoleException() {super(MESSAGE);}
}
