package com.github.ogaltsov.amzscouttesttask.controller;

import com.github.ogaltsov.amzscouttesttask.exception.UserRequestOutOfQuotaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserRequestOutOfQuotaException.class)
    public final ResponseEntity<Object> handleUserRequestOutOfQuotaException(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }
}
