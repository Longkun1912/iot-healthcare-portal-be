package com.example.iothealth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "health_recommendations")
@Getter
@Setter
@NoArgsConstructor
public class HealthRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private String heart_rate_impact;

    @Column
    private String blood_pressure_impact;

    @Column
    private String temperature_impact;

    @Column
    private String description;

    @Column
    private String guide_link;
}
