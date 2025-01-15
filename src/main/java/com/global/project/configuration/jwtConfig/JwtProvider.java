package com.global.project.configuration.jwtConfig;

import com.global.project.configuration.AccountDetailsImpl;
import com.global.project.entity.Account;
import com.global.project.enums.EnumAccountVerifyStatus;
import com.global.project.enums.EnumTokenType;
import com.global.project.repository.AccountRepository;
import com.global.project.utils.DateConversion;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtProvider {
    @Autowired
    private AccountRepository accountRepository;

    @Value("${jwt.SECRET_ACCESS_TOKEN_KEY}")
    private String JWT_SECRET_ACCESS_TOKEN;

    @Value("${jwt.JWT_EXPIRATION_ACCESS_TOKEN}")
    private int JWT_EXPIRATION_ACCESS_TOKEN;

    @Value("${jwt.SECRET_REFRESH_TOKEN_KEY}")
    private String JWT_SECRET_REFRESH_TOKEN;

    @Value("${jwt.JWT_EXPIRATION_REFRESH_TOKEN}")
    private int JWT_EXPIRATION_REFRESH_TOKEN;

    @Getter
    @Value("${jwt.SECRET_FORGOT_PASSWORD_TOKEN_KEY}")
    private String JWT_SECRET_FORGOT_PASSWORD_TOKEN;

    @Getter
    @Value("${jwt.JWT_EXPIRATION_FORGOT_PASSWORD_TOKEN}")
    private int JWT_EXPIRATION_FORGOT_PASSWORD_TOKEN;

    public JwtProvider(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String generateToken(AccountDetailsImpl customDetailService) {
        return this.generateTokenByUsername(customDetailService.getUsername());
    }

    public String generateTokenByUsername(String Username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_ACCESS_TOKEN);
        Account account = accountRepository.findByUser_Username(Username).get();
        return Jwts.builder()
                .setSubject(Long.toString(account.getId()))
                .claim("username", account.getUser().getUsername())
                .claim("role", account.getRole().getName())
                .claim("token_type", EnumTokenType.AccessToken.getValue())
                .claim("account_status", EnumAccountVerifyStatus.Verified.getValue())
                .setExpiration(expiryDate)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET_ACCESS_TOKEN)
                .compact();
    }


    public String generateRefreshTokenByUsername(LocalDateTime iat, String Username) {

        Date iatDate = DateConversion.convertLocalDateTimeToDate(iat);

        Date expiryDate = new Date(iatDate.getTime() + JWT_EXPIRATION_REFRESH_TOKEN);

        Account account = accountRepository.findByUser_Username(Username).get();
        return Jwts.builder()
                .setSubject(Long.toString(account.getId()))
                .claim("username", account.getUser().getUsername())
                .claim("token_type", EnumTokenType.RefreshToken.getValue())
                .claim("account_status", EnumAccountVerifyStatus.Verified.getValue())
                .setExpiration(expiryDate)
                .setIssuedAt(iatDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET_REFRESH_TOKEN)
                .compact();
    }

    public String generateForgotPasswordTokenByUsername(String Username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_FORGOT_PASSWORD_TOKEN);
        Account account = accountRepository.findByUser_Username(Username).get();
        return Jwts.builder()
                .setSubject(Long.toString(account.getId()))
                .claim("username", account.getUser().getUsername())
                .claim("token_type", EnumTokenType.ForgotPasswordToken.getValue())
                .claim("account_status", EnumAccountVerifyStatus.Verified.getValue())
                .setExpiration(expiryDate)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET_FORGOT_PASSWORD_TOKEN)
                .compact();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET_ACCESS_TOKEN)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public <T> T getKeyByValueFromJWT(String jwtSecretKey, String key, String token, Class<T> clazz) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.get(key, clazz);
    }

    public String getUsernameContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET_ACCESS_TOKEN)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username", String.class);
    }

}
