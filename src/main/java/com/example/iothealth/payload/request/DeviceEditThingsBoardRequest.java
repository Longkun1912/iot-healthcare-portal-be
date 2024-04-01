package com.example.iothealth.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceEditThingsBoardRequest {
    private IdObjectDevice id;
    private String name;
}
