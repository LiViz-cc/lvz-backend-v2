package com.liviz.v2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnauthenticatedException extends ResponseStatusException {

    public UnauthenticatedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

}
