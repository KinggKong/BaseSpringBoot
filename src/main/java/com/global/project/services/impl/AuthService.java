package com.global.project.services.impl;

import com.global.project.configuration.AccountDetailsImpl;
import com.global.project.configuration.jwtConfig.JwtProvider;
import com.global.project.dto.ApiResponse;
import com.global.project.dto.SignInResponse;
import com.global.project.entity.BlackToken;
import com.global.project.entity.RefreshToken;
import com.global.project.exception.AppException;
import com.global.project.exception.ErrorCode;
import com.global.project.model.SignInRequest;
import com.global.project.repository.AccountRepository;
import com.global.project.repository.BlackTokenRepository;
import com.global.project.repository.IRefreshTokenRepository;
import com.global.project.services.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtUtils;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRefreshTokenRepository iRefreshTokenRepository;
    private final BlackTokenRepository blackTokenRepository;

    @Value("${jwt.JWT_EXPIRATION_REFRESH_TOKEN}")
    private int jwtExpirationRefreshToken;

    @Value("${facebook.openapi.staging-url}")
    @NonFinal
    private String DOMAIN_NAME;

    public AuthService(AuthenticationManager authenticationManager, JwtProvider jwtUtils, AccountRepository accountRepository, PasswordEncoder passwordEncoder, IRefreshTokenRepository iRefreshTokenRepository, BlackTokenRepository blackTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.iRefreshTokenRepository = iRefreshTokenRepository;
        this.blackTokenRepository = blackTokenRepository;
    }

    @Override
    public ResponseEntity<ApiResponse<SignInResponse>> login(SignInRequest signinRequest, HttpServletRequest request) {
        if (accountRepository.checkIsActive(signinRequest.getEmail()).isEmpty()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        try {
            Authentication authentication = authenticateUser(signinRequest);
            AccountDetailsImpl accountDetails = (AccountDetailsImpl) authentication.getPrincipal();
            Long accountId = accountDetails.getAccount().getId();
            if (!isValidEmail(signinRequest.getEmail().trim())) {
                throw new AppException(ErrorCode.EMAIL_INCORRECT_FORMAT);
            }
            if (signinRequest.getPassword().length() < 8) {
                throw new AppException(ErrorCode.PASSWORD_SIGNUP_TOO_SHORT);
            }
            LocalDateTime now = LocalDateTime.now();

            String accountToken = jwtUtils.generateTokenByUsername(accountDetails.getUsername());

            String refreshToken = jwtUtils.generateRefreshTokenByUsername(now, accountDetails.getUsername());


            SignInResponse signinResponse = SignInResponse.builder()
                    .id(accountId)
                    .type("Bearer")
                    .accessToken(accountToken)
                    .refreshToken(refreshToken)
                    .username(accountDetails.getUsername())
                    .fullname(accountDetails.getAccount().getUser().getFullname())
                    .email(accountDetails.getAccount().getEmail())
                    .isActive(accountDetails.getAccount().getIsActive())
                    .roleName(accountDetails.getRoleName())
                    .avatar(accountDetails.getAccount().getUser().getAvatar() != null && !accountDetails.getAccount().getUser().getAvatar().isEmpty() ? DOMAIN_NAME + accountDetails.getAccount().getUser().getAvatar() : null)
                    .build();

            ApiResponse<SignInResponse> apiResponse = ApiResponse.<SignInResponse>builder()
                    .data(signinResponse)
                    .message("Login success")
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (Exception e) {
            throw new AppException(ErrorCode.LOGIN_FAILED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<SignInResponse>> loginAdmin(SignInRequest signinRequest, HttpServletRequest request) {
        if (accountRepository.checkIsActive(signinRequest.getEmail()).isEmpty()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        try {
            Authentication authentication = authenticateUser(signinRequest);
            AccountDetailsImpl accountDetails = (AccountDetailsImpl) authentication.getPrincipal();

            if (!accountDetails.getAccount().getRole().getName().equalsIgnoreCase("ROLE_ADMIN")) {
                throw new AppException(ErrorCode.NO_ROLE_ACCESS);
            }

            Long accountId = accountDetails.getAccount().getId();
            if (!isValidEmail(signinRequest.getEmail().trim())) {
                throw new AppException(ErrorCode.EMAIL_INCORRECT_FORMAT);
            }
            if (signinRequest.getPassword().length() < 8) {
                throw new AppException(ErrorCode.PASSWORD_SIGNUP_TOO_SHORT);
            }
            LocalDateTime now = LocalDateTime.now();


            String accountToken = jwtUtils.generateTokenByUsername(accountDetails.getUsername());

            String refreshToken = jwtUtils.generateRefreshTokenByUsername(now, accountDetails.getUsername());


            SignInResponse signinResponse = SignInResponse.builder()
                    .id(accountId)
                    .type("Bearer")
                    .accessToken(accountToken)
                    .refreshToken(refreshToken)
                    .username(accountDetails.getUsername())
                    .fullname(accountDetails.getAccount().getUser().getFullname())
                    .email(accountDetails.getAccount().getEmail())
                    .isActive(accountDetails.getAccount().getIsActive())
                    .roleName(accountDetails.getRoleName())
                    .avatar(accountDetails.getAccount().getUser().getAvatar() != null && !accountDetails.getAccount().getUser().getAvatar().isEmpty() ? DOMAIN_NAME + accountDetails.getAccount().getUser().getAvatar() : null)
                    .build();

            ApiResponse<SignInResponse> apiResponse = ApiResponse.<SignInResponse>builder()
                    .data(signinResponse)
                    .message("Login success")
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (Exception e) {
            throw new AppException(ErrorCode.LOGIN_FAILED);
        }
    }

    @Override
    public ApiResponse<?> logout(HttpServletRequest request) {
        String jwtToken = getJwtFromRequest(request);
        if (jwtToken == null) {
            return ApiResponse.builder()
                    .data("logout failed")
                    .message("logout failed")
                    .build();
        }
        blackTokenRepository.save(BlackToken.builder()
                .token(jwtToken)
                .build());
        return ApiResponse.builder()
                .data("logout successfully")
                .message("logout successfully")
                .build();
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    private void saveOrUpdateRefreshToken(Long accountId, String refreshToken, LocalDateTime now, String deviceInfo) {
        RefreshToken existingToken = iRefreshTokenRepository.findByAccountIdAndDeviceInfo(accountId, deviceInfo);
        if (existingToken != null) {
            existingToken.setToken(refreshToken);
            existingToken.setCreatedAt(now);
            existingToken.setExp(now.plus(Duration.ofMillis(jwtExpirationRefreshToken)));
            existingToken.setIat(now);
            iRefreshTokenRepository.save(existingToken);
        } else {
            RefreshToken refreshTokenModel = RefreshToken.builder()
                    .accountId(accountId)
                    .token(refreshToken)
                    .createdAt(now)
                    .exp(now.plus(Duration.ofMillis(jwtExpirationRefreshToken)))
                    .iat(now)
                    .deviceInfo(deviceInfo)
                    .build();
            iRefreshTokenRepository.save(refreshTokenModel);
        }
    }

    private Authentication authenticateUser(SignInRequest signinRequest) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
    }


    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

}