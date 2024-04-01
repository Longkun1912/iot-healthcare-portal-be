package com.example.iothealth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerId {
    private String id;
    private String entityType;
}
