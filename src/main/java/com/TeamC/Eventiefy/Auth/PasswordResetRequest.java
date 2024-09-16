package com.TeamC.Eventiefy.Auth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PasswordResetRequest {
    private String token;
    private String newPassword;

}
