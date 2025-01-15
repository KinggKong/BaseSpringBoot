package com.global.project.restController;

import com.global.project.dto.ApiResponse;
import com.global.project.dto.SignInResponse;
import com.global.project.model.*;
import com.global.project.services.IAccountService;
import com.global.project.services.IAuthService;
import com.global.project.services.IAuthenticationService;
import com.global.project.utils.Const;
import com.global.project.utils.LoginHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "01. AUTH")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/auth")
public class RestAuthController {
    @Autowired
    IAuthenticationService authenticationService;
    @Autowired
    IAccountService accountService;
    @Autowired
    IAuthService iAuthService;
    @Autowired
    LoginHelper loginHelper;


    @Operation(summary = "signin", description = "singin to system", tags = {"01. AUTH"})
    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@RequestBody SignInRequest signInRequest, HttpServletRequest request) {
        return iAuthService.login(signInRequest, request);
    }


    @PostMapping("/via-email")
    public ApiResponse<?> viaEmailToResgister(@RequestBody EmailModel emailModel) {
        return ApiResponse.builder()
                .data(authenticationService.handleSendCodeToMail(emailModel.getEmail()))
                .message("Send code to maill successfulll")
                .build();
    }

    @PostMapping("/verifyCode")
    public ResponseEntity<?> checkCode(@RequestBody ViaCodeModel viaCodeModel) {
        return accountService.verifyCode(viaCodeModel);
    }

    @PostMapping("/sign-up")
    public ApiResponse<?> signUp(@RequestBody SignupRequest signupRequest) {
        return ApiResponse.builder()
                .data(accountService.registerAccount(signupRequest))
                .message("Successfully registered")
                .build();
    }

    @GetMapping("/forgot-password")
    public ApiResponse<?> forgotPassword(@RequestParam String email) throws MessagingException {
        return ApiResponse.builder()
                .data(authenticationService.sendTokenForgotPassword(email))
                .message("send token to reset password successfully")
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<?> changePassword(@RequestBody ChangePasswordRequest changePasswrodRequest) {
        return ApiResponse.builder()
                .data(authenticationService.resetPassword(changePasswrodRequest))
                .message("change password successfully")
                .build();
    }

    @GetMapping("/oauth/google")
    public void hanldeLoginWithGoogle(@RequestParam("code") String code,
                                      @RequestParam("scope") String scope,
                                      @RequestParam("authuser") String authUser,
                                      @RequestParam("prompt") String prompt,
                                      HttpServletResponse response) throws IOException {

        SignInResponse signInResponse = loginHelper.processGrantCode(code);
        if (signInResponse != null) {
            String redirectUrl = "http://localhost:5173/login-success?token=" + signInResponse.getAccessToken()
                    + "&refreshToken=" + signInResponse.getRefreshToken();
            response.sendRedirect(redirectUrl);
        } else {
            String redirectUrl = "http://localhost:5173/login-success";
            response.sendRedirect(redirectUrl);
        }
    }


    @PostMapping("/update-password")
    public ApiResponse<?> updatePassword(@RequestBody NewPassword newPassword) {
            return accountService.updatePassword(newPassword);
    }

    @Operation(summary = "signin", description = "singin to system", tags = {"01. AUTH"})
    @PostMapping("/admin/sign-in")
    public ResponseEntity<?> authenticateUserAdmin(@RequestBody SignInRequest signInRequest, HttpServletRequest request) {
        return iAuthService.loginAdmin(signInRequest, request);
    }

    @GetMapping("/logout")
    public ApiResponse<?> logout(HttpServletRequest request){
        return iAuthService.logout(request);
    }

}
