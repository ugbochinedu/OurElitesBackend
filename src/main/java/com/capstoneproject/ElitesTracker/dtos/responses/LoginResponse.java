package com.capstoneproject.ElitesTracker.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponse {
    private String semicolonEmail;
    private String firstName;
    private String message;
    private String jwtToken;
    private boolean isLoggedIn;
}
