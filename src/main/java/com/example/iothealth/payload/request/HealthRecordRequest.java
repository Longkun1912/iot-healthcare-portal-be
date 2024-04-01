package com.example.iothealth.payload.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class HealthRecordRequest {
    @JsonProperty
    @NotEmpty(message = "This field cannot be empty.")
    private String id;

    @JsonProperty
    @NotEmpty(message = "This field cannot be empty.")
    private String date;
}
