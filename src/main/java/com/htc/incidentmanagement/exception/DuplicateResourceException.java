package com.htc.incidentmanagement.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
     public DuplicateResourceException(String resource, String field) {
        super(String.format("%s already exists with %s", resource, field));
    }
}