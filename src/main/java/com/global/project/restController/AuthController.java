package com.global.project.restController;

import com.global.project.configuration.SigninRequest;
import com.global.project.configuration.SigninResponse;
import com.global.project.entity.User;
import com.global.project.repository.RoleRepository;
import com.global.project.repository.UserRepository;
import com.global.project.service.IUserService;
import com.global.project.configuration.JwtConfig.JwtProvider;
import com.global.project.configuration.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtUtils;
    @Autowired
    IUserService iUserService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody SigninRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateTokenByUsername(userDetails.getUsername());
            return ResponseEntity.ok(new SigninResponse(userDetails.getUser().getId(), "Bearer", jwt, userDetails.getUsername(), userDetails.getUser().getEmail(),
                    userDetails.getUser().getActive(), userDetails.getUser().getAvatar(), userDetails.getRoleName()));
    }
    @GetMapping("/resetPassAdmin")
    public String resetPassAdmin(){
        User user = userRepository.findByUsername("admin").orElse(null);
        user.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(user);
        return "";
    }
}
