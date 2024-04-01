package com.example.iothealth.payload.request;

import com.example.iothealth.domain.CustomerId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerUpdateRequest {
    private CustomerId id;
    private String title;
    private String phone;
    private String email;

}
