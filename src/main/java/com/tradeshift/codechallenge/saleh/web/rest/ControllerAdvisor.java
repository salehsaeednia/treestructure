package com.tradeshift.codechallenge.saleh.web.rest;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.tradeshift.codechallenge.saleh.exception.BaseException;
import com.tradeshift.codechallenge.saleh.exception.ResultError;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor  {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public RestResult<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new RestResult<>(ResultError.InvalidArgument, errors);
    }

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public RestResult<Map<String, String>> handleApplicationExceptions(BaseException ex) {
        return new RestResult<>(ex.getError(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected RestResult<Map<String, String>> handleUnrecognizedPropertyException(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof UnrecognizedPropertyException) {
            Map<String, String> errors = new HashMap<>();
            String fieldName = ((UnrecognizedPropertyException) rootCause).getPropertyName();
            String errorMessage = "Unknown field";
            errors.put(fieldName, errorMessage);
            return new RestResult<>(ResultError.InvalidArgument, errors);
        }
        return new RestResult<>(ResultError.InvalidArgument, null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public RestResult<Map<String, String>> handleUnsupportedExceptions(Exception ex) {
        return new RestResult<>(ResultError.InternalError, null);
    }
}
