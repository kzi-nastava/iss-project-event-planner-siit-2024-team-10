package com.ftn.iss.eventPlanner.dto.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String email;
    private String password;

    public LoginRequestDTO() {}
}

