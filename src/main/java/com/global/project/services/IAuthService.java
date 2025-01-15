package com.global.project.services;

import com.global.project.dto.ApiResponse;
import com.global.project.dto.SignInResponse;
import com.global.project.model.SignInRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<ApiResponse<SignInResponse>> login(SignInRequest signinRequest, HttpServletRequest request);

    ResponseEntity<ApiResponse<SignInResponse>> loginAdmin(SignInRequest signinRequest, HttpServletRequest request);

    ApiResponse<?> logout(HttpServletRequest request);
}
