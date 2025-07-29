package com.ftn.iss.eventPlanner.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private int expiresIn;
}
