package com.exam.kakaopay.controller;

import com.exam.kakaopay.exception.AccessDeniedException;
import com.exam.kakaopay.exception.NotFoundException;
import com.exam.kakaopay.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import com.exam.kakaopay.constants.Codes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

   @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(ApiResponse.of(Codes.E0010.code, ex.getMessage()), headers, status);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiResponse validation(ValidationException e) {
        return ApiResponse.of(Codes.E0020.code, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ApiResponse validation(AccessDeniedException e) {
        return ApiResponse.of(Codes.E0030.code, e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiResponse validation(NotFoundException e) {
        return ApiResponse.of(Codes.E0040.code, e.getMessage());
    }


}
