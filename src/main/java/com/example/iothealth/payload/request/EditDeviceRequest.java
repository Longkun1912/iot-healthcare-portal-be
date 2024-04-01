package com.example.iothealth.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class EditDeviceRequest {
    private UUID id;
    private String name;

}
