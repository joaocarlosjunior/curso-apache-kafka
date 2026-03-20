package com.joaocarlos.products.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class ProductsExceptionHandler {

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ErrorMessage> KafkaException(KafkaException e) {
        ErrorMessage error = new ErrorMessage(new Date(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
