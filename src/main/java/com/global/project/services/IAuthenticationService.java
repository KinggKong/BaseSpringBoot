package com.global.project.services;

import com.global.project.dto.AccountResponse;
import com.global.project.entity.ViaCode;
import com.global.project.model.ChangePasswordRequest;
import jakarta.mail.MessagingException;

public interface IAuthenticationService {
    int generateCode();

    int sendCodeToEmail(String to, String subject, String content);

    ViaCode insertCode(int code, String email);

    String handleSendCodeToMail(String email);

    String sendTokenForgotPassword(String email) throws MessagingException;

    AccountResponse resetPassword(ChangePasswordRequest changePasswrodRequest);
}
