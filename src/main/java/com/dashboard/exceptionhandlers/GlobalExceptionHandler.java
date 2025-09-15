package com.dashboard.exceptionhandlers;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        // Optional: aggregate field errors into pd.setProperty("errors", ...)
        pd.setDetail(ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
        pd.setType(URI.create("https://example.com/problems/validation-error"));
        return pd;
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    ProblemDetail handleNotFound(ChangeSetPersister.NotFoundException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Resource not found");
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create("https://example.com/problems/not-found"));
        return pd;
    }
}
