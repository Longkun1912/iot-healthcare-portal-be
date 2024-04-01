package com.example.iothealth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "health_objectives")
@Getter
@Setter
@NoArgsConstructor
public class HealthObjective {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String title;

    @Column
    private String image;

    @Column
    private int heart_rate;

    @Column
    private int blood_pressure;

    @Column
    private float temperature;

    @Column
    private String description;

    @Column
    private String information_url;

    @OneToMany(mappedBy = "health_objective")
    @JsonManagedReference
    private List<User> users;
}
