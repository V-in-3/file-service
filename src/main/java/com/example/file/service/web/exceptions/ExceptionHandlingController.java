package com.example.file.service.web.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("[e] Request validation failed", e);
        return new ResponseEntity<>(new ApiErrorMessage(false, "Argument not valid exception"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorMessage> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("[e] Http message not readable exception", e);
        return new ResponseEntity<>(new ApiErrorMessage(false, "Http message not readable exception"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ApiErrorMessage> handleFileNotFoundException(FileNotFoundException e) {
        log.error("[e] File not found exception", e);
        return new ResponseEntity<>(new ApiErrorMessage(false, "file not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<ApiErrorMessage> handleTagNotFoundException(TagNotFoundException e) {
        log.error("[e] Tag not found exception", e);
        return new ResponseEntity<>(new ApiErrorMessage(false, "tag not found on file"), HttpStatus.BAD_REQUEST);
    }
}
