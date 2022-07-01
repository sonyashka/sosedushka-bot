package com.ppobot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SkillException extends BaseException {
    private final static String MESSAGE = "Has no skill";

    public SkillException(Throwable t) {
        super(MESSAGE, t);
    }

    public SkillException() {super(MESSAGE);}
}