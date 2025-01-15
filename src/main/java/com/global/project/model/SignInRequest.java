package com.global.project.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignInRequest {
    @Schema(title = "email", example = "anhdeptrai7749@gmail.com")
    @NotBlank(message = "")

    private String email;

    @Schema(title = "password", example = "11111111")
    @NotBlank
    private String password;
}
