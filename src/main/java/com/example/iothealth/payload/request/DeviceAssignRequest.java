package com.example.iothealth.payload.request;

import com.example.iothealth.domain.CustomerId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceAssignRequest {
    private String customerId;
    private String deviceId;
}
