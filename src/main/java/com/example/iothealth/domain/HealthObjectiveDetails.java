package com.example.iothealth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HealthObjectiveDetails {
    @JsonProperty
    private int id;

    @JsonProperty
    private String title;

    @JsonProperty
    private String image;

    @JsonProperty
    private int heart_rate;

    @JsonProperty
    private int blood_pressure;

    @JsonProperty
    private float temperature;

    @JsonProperty
    private String description;

    @JsonProperty
    private String information_url;
}
