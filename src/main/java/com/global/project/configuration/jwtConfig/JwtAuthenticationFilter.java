package com.global.project.configuration.jwtConfig;

//import com.global.project.configuration.AccountDetailsImpl;

import com.global.project.configuration.AccountDetailsImpl;
import com.global.project.enums.EnumAccountVerifyStatus;
import com.global.project.enums.EnumTokenType;
import com.global.project.exception.AppException;
import com.global.project.exception.ErrorCode;
import com.global.project.repository.AccountRepository;
import com.global.project.repository.BlackTokenRepository;
import com.global.project.services.impl.AccountService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    BlackTokenRepository blackTokenRepository;


    @Value("${jwt.SECRET_ACCESS_TOKEN_KEY}")
    private String JWT_SECRET_ACCESS_TOKEN;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = getJwtFromRequest(request);
            if (jwtToken != null && this.validateToken(JWT_SECRET_ACCESS_TOKEN, jwtToken)) {
                checkTokenIsInBlackToken(jwtToken);
                String username = jwtProvider.getKeyByValueFromJWT(JWT_SECRET_ACCESS_TOKEN, "username", jwtToken, String.class);
                int tokenType = jwtProvider.getKeyByValueFromJWT(JWT_SECRET_ACCESS_TOKEN, "token_type", jwtToken, Integer.class);
                int accountStatus = jwtProvider.getKeyByValueFromJWT(JWT_SECRET_ACCESS_TOKEN, "account_status", jwtToken, Integer.class);
                AccountDetailsImpl accountDetails = (AccountDetailsImpl) accountService.loadUserByUsername(username);
                if (accountDetails != null) {
                    UsernamePasswordAuthenticationToken
                            authentication = new UsernamePasswordAuthenticationToken(accountDetails, null, accountDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public boolean validateToken(String jwtTokenSecret, String token) {
        try {
            Jwts.parser().setSigningKey(jwtTokenSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    public void checkTokenIsInBlackToken(String token) {
        if (blackTokenRepository.existsByToken(token)) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }
}
