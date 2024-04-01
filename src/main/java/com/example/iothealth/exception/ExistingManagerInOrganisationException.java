package com.example.iothealth.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientException;

@ResponseStatus(code = org.springframework.http.HttpStatus.BAD_REQUEST)
public class ExistingManagerInOrganisationException extends RestClientException {
    public ExistingManagerInOrganisationException() {
        super("Manager already exists in the organisation");
    }
}
