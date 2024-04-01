package com.example.iothealth.domain;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class HealthAnalysis {
    private Set<HealthRecommendationDetails> recommendations = new HashSet<>();

    private String heartRateStatus;

    private String bloodPressureStatus;

    private String temperatureStatus;

    private String overallStatus;
}
