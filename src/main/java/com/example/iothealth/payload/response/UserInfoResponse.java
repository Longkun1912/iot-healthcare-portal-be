package com.example.iothealth.payload.response;

import com.example.iothealth.model.Organisation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserInfoResponse {
    private UUID id;
    private String username;
    private String avatar;
    private String mobile;
    private String gender;
    private String email;
    private String password;
    private List<String> roles;
    private String organisation;
    private String jwt_token;

    public UserInfoResponse(UUID id, String username, String avatar, String mobile, String password, String gender, String email, List<String> roles, String organisation, String jwt_token) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.mobile = mobile;
        this.password = password;
        this.gender = gender;
        this.email = email;
        this.roles = roles;
        this.organisation = organisation;
        this.jwt_token = jwt_token;
    }
}
