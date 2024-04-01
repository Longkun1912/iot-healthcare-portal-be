package com.example.iothealth.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class HealthProgress {
    private UUID userId;

    private HealthRecordDetails currentHealth;

    private HealthObjectiveDetails targetedHealth;

    private HealthAnalysis healthAnalysis;

    public HealthProgress(UUID userId) {
        this.userId = userId;
    }
}
