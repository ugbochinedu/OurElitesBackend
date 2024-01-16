package com.capstoneproject.ElitesTracker.dtos.requests;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    private String semicolonEmail;
    private String newPassword;
    private String token;
}
