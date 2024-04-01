package com.example.iothealth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private CustomerId id;
    private Long createdTime;
    private AdditionalInfo additionalInfo;
    private String country;
    private String state;
    private String city;
    private String address;
    private String address2;
    private String zip;
    private String phone;
    private String email;
    private String title;
    private TenantId tenantId;
    private String externalId;
    private String name;

}
