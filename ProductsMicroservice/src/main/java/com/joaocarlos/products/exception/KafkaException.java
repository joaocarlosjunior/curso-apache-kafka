package com.joaocarlos.products.exception;

public class KafkaException extends RuntimeException{
    private String details;
    public KafkaException(String message, String details) {
        super(message);
        this.details = details;
    }

    public String getDetails() {
        return details;
    }
}
