package com.example.iothealth.payload.response;

import lombok.Data;

@Data
public class UserDeviceResponse {
    private String id;
    private String name;
    private String label;
    private String picture;
    private boolean is_gateway;
    private boolean is_active;
    private String type;
    private String additional_info;
    private String created_time;
    private String last_updated;
    private String ownerUserName;
}
