package com.global.project.WebConfiguration.JwtConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtResponse {
    private Long id;
    private String type = "Bearer";
    private String token;
    private String username;
    private String email;
    private Boolean isActive;
    private String avatar;
    private String roleName;
}
