package com.example.iothealth.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddHealthObjectiveRequest {
    @JsonProperty
    private String title;

    @JsonProperty
    private MultipartFile picture;

    @JsonProperty
    private int heart_rate;

    @JsonProperty
    private int blood_pressure;

    @JsonProperty
    private int temperature;

    @JsonProperty
    private String description;

    @JsonProperty
    private String information_url;
}
