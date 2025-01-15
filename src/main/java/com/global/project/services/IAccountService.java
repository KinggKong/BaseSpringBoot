package com.global.project.services;

import com.global.project.dto.AccountResponse;
import com.global.project.dto.ApiResponse;
import com.global.project.model.NewPassword;
import com.global.project.model.SignupRequest;
import com.global.project.model.ViaCodeModel;
import org.springframework.http.ResponseEntity;

public interface IAccountService {
    ApiResponse<AccountResponse> registerAccount(SignupRequest signupRequest);

    ResponseEntity<?> verifyCode(ViaCodeModel viaCodeModel);

    ApiResponse<?> updatePassword(NewPassword newPassword);
}
