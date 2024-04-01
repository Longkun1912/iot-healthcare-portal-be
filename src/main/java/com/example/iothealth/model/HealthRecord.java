package com.example.iothealth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "health_records")
@Getter
@Setter
@NoArgsConstructor
public class HealthRecord {
    @Id
    private UUID id;

    @Column
    private int heart_rate;

    @Column
    private int blood_pressure;

    @Column
    private float temperature;

    @Column
    private LocalDateTime last_updated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
