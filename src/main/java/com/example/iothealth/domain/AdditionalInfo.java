package com.example.iothealth.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdditionalInfo {
    private List<String> emergencyEmail;

    public AdditionalInfo() {

    }

    public AdditionalInfo(List<String> emergencyEmail) {
        this.emergencyEmail = emergencyEmail;
    }
}
