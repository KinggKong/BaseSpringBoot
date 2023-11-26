package com.global.project.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SigninResponse {
    private Long id;
    private String type = "Bearer";
    private String token;
    private String username;
    private String email;
    private Boolean isActive;
    private String avatar;
    private String roleName;
}
