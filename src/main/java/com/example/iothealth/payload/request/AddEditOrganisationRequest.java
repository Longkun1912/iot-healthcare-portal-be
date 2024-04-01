package com.example.iothealth.payload.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AddEditOrganisationRequest {
    @NotEmpty(message = "This field cannot be empty.")
    private String organisationName;

    @NotEmpty(message = "This field cannot be empty.")
    private String orgnisationDescription;

    @NotEmpty(message = "This field cannot be empty.")
    private String organisationAdress;

    @NotEmpty(message = "This field cannot be empty.")
    private String organisationContactNumber;

}
