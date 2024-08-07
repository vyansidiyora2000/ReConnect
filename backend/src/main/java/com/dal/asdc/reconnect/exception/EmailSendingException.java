package com.dal.asdc.reconnect.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailSendingException extends RuntimeException {
    private final HttpStatus status;

    public EmailSendingException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
