package com.example.iothealth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "devices")
@Getter
@Setter
public class Device {
    @Id
    private UUID id;

    @Column
    private String name;

    @Column
    private String label;

    @Column
    private String image;

    @Column
    private boolean is_gateway;

    @Column
    private boolean is_active;

    @Column
    private String type;

    @Column
    private String device_profile_name;

    @Column
    private String additional_info;

    @Column
    private LocalDateTime created_time;

    @Column
    private LocalDateTime last_updated;

    @ManyToOne
    @JoinColumn(name = "organisation")
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name = "device_owner")
    private User owner;
}
