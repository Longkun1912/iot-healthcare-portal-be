package com.example.iothealth.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantId {
    private String entityType;
    private String id;


    public TenantId() {

    }

    public TenantId(String entityType, String id) {
        this.entityType = entityType;
        this.id = id;
    }
}