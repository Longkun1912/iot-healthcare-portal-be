package com.example.iothealth.payload.request;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerCreatingRequest {
    private String title;
    private String phone;
    private String email;
}

