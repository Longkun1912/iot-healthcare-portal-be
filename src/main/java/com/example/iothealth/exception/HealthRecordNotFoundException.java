package com.example.iothealth.exception;

import org.springframework.web.client.RestClientException;

public class HealthRecordNotFoundException extends RestClientException {
    public HealthRecordNotFoundException(String message) {
        super(message);
    }
}
