package com.liviz.v2.exception;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuchElementFoundException extends ResponseStatusException {

    public NoSuchElementFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
