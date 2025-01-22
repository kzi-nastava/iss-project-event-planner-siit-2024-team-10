package com.ftn.iss.eventPlanner.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDTO {
    @Size(min = 8, message = "Old password must be at least 8 characters long")
    private String oldPassword;
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;

    public ChangePasswordDTO() {}
}
