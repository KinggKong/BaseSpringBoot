package com.global.project.services.impl;

import com.global.project.configuration.jwtConfig.JwtProvider;
import com.global.project.dto.AccountResponse;
import com.global.project.entity.Account;
import com.global.project.entity.ViaCode;
import com.global.project.exception.AppException;
import com.global.project.exception.ErrorCode;
import com.global.project.mapper.AccountMapper;
import com.global.project.model.ChangePasswordRequest;
import com.global.project.repository.AccountRepository;
import com.global.project.repository.ViaCodeRepository;
import com.global.project.services.IAuthenticationService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class AuthenticationService implements IAuthenticationService {
    JavaMailSender javaMailSender;
    ViaCodeRepository viaCodeRepository;
    AccountRepository accountRepository;
    JwtProvider jwtProvider;
    PasswordEncoder passwordEncoder;
    AccountMapper accountMapper;

    @Value("${facebook.openapi.url-client}")
    @NonFinal
    private String url;


    @Override
    public int generateCode() {
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000);
        return randomNumber;
    }

    @Override
    public int sendCodeToEmail(String to, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            javaMailSender.send(message);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public ViaCode insertCode(int code, String email) {
        ViaCode vi = ViaCode.builder()
                .viaCode(code)
                .email(email.trim())
                .createdAt(LocalDateTime.now())
                .build();
        return viaCodeRepository.save(vi);
    }

    @Override
    public String handleSendCodeToMail(String email) {
        if (!isValidEmail(email.trim())) {
            throw new AppException(ErrorCode.EMAIL_INCORRECT_FORMAT);
        }
        if (accountRepository.existsByEmail(email.trim())) {
            throw new AppException(ErrorCode.ACCOUNT_EMAIL_EXISTED);
        }
        try {
            int code = generateCode();
            int result = sendCodeToEmail(email, "Code to verifiy accout", "Code to sign up for you is: " + code);
            insertCode(code, email);
            return "send code to email successfully";
        } catch (Exception e) {
            return "send code to email failed" + e.getMessage();
        }
    }

    @Override
    public String sendTokenForgotPassword(String email) throws MessagingException {
        Optional<Account> optional = accountRepository.findByEmail(email);

        if (!isValidEmail(email.trim())) {
            throw new AppException(ErrorCode.EMAIL_INCORRECT_FORMAT);
        }
        if (optional.isEmpty()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        Account account = optional.get();
        String token = jwtProvider.generateForgotPasswordTokenByUsername(account.getUser().getUsername());

        account.setForgotPasswordToken(token);
        accountRepository.saveAndFlush(account);

        String subtitle = "Forgot Password";
        String passwordResetLink = url + "/reset-password?token=" + token;
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Password Reset</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: Arial, sans-serif;\n" +
                "      background-color: #f4f4f4;\n" +
                "      color: #333;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "    }\n" +
                "    .container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 0 auto;\n" +
                "      background-color: #ffffff;\n" +
                "      padding: 20px;\n" +
                "      border-radius: 5px;\n" +
                "      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"container\">\n" +
                "    <h2>Password Reset Request</h2>\n" +
                "    <p>Hello,</p>\n" +
                "    <p>We received a request to reset your password. Click the button below to set a new password:</p>\n" +
                "    <p><a href=\"{{passwordResetLink}}\" style=\"display: inline-block; padding: 10px 20px; color: #ffffff; background-color: #007bff; border-radius: 5px; text-decoration: none;\">Reset Password</a></p>\n" +
                "    <p>If you didn’t request a password reset, please ignore this email.</p>\n" +
                "    <p>Thank you, <br>Your Website Team</p>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";


        html = html.replace("{{passwordResetLink}}", passwordResetLink);

        helper.setTo(email);
        helper.setSubject(subtitle);
        helper.setText(html, true);

        javaMailSender.send(message);
        return "send token to reset password successfully";
    }

    @Override
    public AccountResponse resetPassword(ChangePasswordRequest changePasswordRequest) {
        String username = jwtProvider.getKeyByValueFromJWT(jwtProvider.getJWT_SECRET_FORGOT_PASSWORD_TOKEN(), "username", changePasswordRequest.getToken(), String.class);
        Optional<Account> optional = accountRepository.findByUser_Username(username);
        if (optional.isEmpty()) {
            throw new AppException(ErrorCode.TOKEN_RESET_PASSWORD_INVALID);
        }
        Account account = optional.get();

        if (account.getForgotPasswordToken() == null) {
            throw new AppException(ErrorCode.TOKEN_RESET_PASSWORD_INVALID);
        }

        if (!validateToken(jwtProvider.getJWT_SECRET_FORGOT_PASSWORD_TOKEN(), changePasswordRequest.getToken())) {
            throw new AppException(ErrorCode.TOKEN_RESET_PASSWORD_INVALID);
        }

        Date expDate = jwtProvider.getKeyByValueFromJWT(
                jwtProvider.getJWT_SECRET_FORGOT_PASSWORD_TOKEN(),
                "exp",
                changePasswordRequest.getToken(),
                Date.class
        );

        if (expDate == null) {
            throw new AppException(ErrorCode.TOKEN_RESET_PASSWORD_INVALID);
        }

        if (changePasswordRequest.getNewPassword().length() < 8) {
            throw new AppException(ErrorCode.NEWPASSWORD_TOO_SHORT);
        }
        LocalDateTime exp = expDate.toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
        if (exp.isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.TOKEN_RESET_PASSWORD_EXPIRED);
        }


        String newPassword = changePasswordRequest.getNewPassword().trim();
        System.out.println("Hashed password from database: " + account.getPassword());
        if (passwordEncoder.matches(newPassword, account.getPassword())) {
            throw new AppException(ErrorCode.NEWPASSWORD_CANT_SAME_OLD_PASSWORD);
        }
        account.setPassword(passwordEncoder.encode(newPassword));
        account.setForgotPasswordToken(null);
        return accountMapper.toResponse(accountRepository.save(account));
    }

    public boolean validateToken(String jwtTokenSecret, String token) {
        try {
            Jwts.parser().setSigningKey(jwtTokenSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
