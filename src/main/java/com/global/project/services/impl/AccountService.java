package com.global.project.services.impl;

import com.global.project.configuration.AccountDetailsImpl;
import com.global.project.dto.AccountResponse;
import com.global.project.dto.ApiResponse;
import com.global.project.entity.Account;
import com.global.project.entity.Role;
import com.global.project.entity.User;
import com.global.project.exception.AppException;
import com.global.project.exception.ErrorCode;
import com.global.project.mapper.AccountMapper;
import com.global.project.model.NewPassword;
import com.global.project.model.SignupRequest;
import com.global.project.model.ViaCodeModel;
import com.global.project.repository.AccountRepository;
import com.global.project.repository.RoleRepository;
import com.global.project.repository.UserRepository;
import com.global.project.services.IAccountService;
import com.global.project.utils.Const;
import com.global.project.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class AccountService implements IAccountService, UserDetailsService {
    AccountRepository accountRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    AccountMapper accountMapper;
    ViaCodeService viaCodeService;
//    IUserDocumentService userDocumentService;

    @Override
    public UserDetails loadUserByUsername(String key) throws UsernameNotFoundException {
        return new AccountDetailsImpl(accountRepository.findByEmailOrPhoneNumberOrUsername(key).get());
    }

    @Override
    public ApiResponse<AccountResponse> registerAccount(SignupRequest signupRequest) {
        validateRegister(signupRequest);

        User user = User.builder()
                .fullname(signupRequest.getFirstName() + " " + signupRequest.getLastName())
                .username(generateUserId("user"))
                .firstname(signupRequest.getFirstName())
                .lastname(signupRequest.getLastName())
                .email(signupRequest.getEmail().trim())
                .gender(signupRequest.getGender())
                .dob(signupRequest.getDob())
                .isActive(true)
                .language("en")
                .build();

        User insertUser = userRepository.saveAndFlush(user);
//        userDocumentService.insertUserDocument(insertUser, new Info(), new ArrayList<>());

        if (insertUser == null) {
            throw new AppException(ErrorCode.USER_CANT_CREATE_USER);
        }

        Role role = roleRepository.findByName(Const.ROLE_USER);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }

        Account account = Account.builder()
                .user(insertUser)
                .username(insertUser.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword().trim()))
                .email(signupRequest.getEmail().trim())
                .role(role)
                .isActive(true)
                .build();

        return ApiResponse.<AccountResponse>builder()
                .data(accountMapper.toResponse(accountRepository.save(account)))
                .message("register successfully")
                .build();

    }

    @Override
    public ResponseEntity<?> verifyCode(ViaCodeModel viaCodeModel) {
        boolean result = viaCodeService.validateCode(viaCodeModel.getCode(), viaCodeModel.getEmail());
        if (result) {
            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .message("Verify code successfully")
                            .build()
            );
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<String>builder()
                        .code(2000)
                        .message("Verify code failed")
                        .build()
        );
    }


    public static String generateUserId(String prefix) {
        String uuid = UUID.randomUUID().toString();
        return prefix + "-" + uuid;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidDob(LocalDate dob) {
        LocalDate today = LocalDate.now();
        if (dob.isAfter(today)) {
            return false;
        }
        int age = Period.between(dob, today).getYears();
        return age >= 18;
    }

    public void validateRegister(SignupRequest signupRequest) {
        if (!isValidEmail(signupRequest.getEmail().trim())) {
            throw new AppException(ErrorCode.EMAIL_INCORRECT_FORMAT);
        }
        if (accountRepository.existsByEmail(signupRequest.getEmail().trim())) {
            throw new AppException(ErrorCode.ACCOUNT_EMAIL_EXISTED);
        }
        if (signupRequest.getPassword().length() < 8) {
            throw new AppException(ErrorCode.PASSWORD_SIGNUP_TOO_SHORT);
        }
        if (signupRequest.getFirstName().isEmpty()) {
            throw new AppException(ErrorCode.FIRTNAME_CANT_BE_EMPTY);
        }
        if (signupRequest.getLastName().isEmpty()) {
            throw new AppException(ErrorCode.LASTNAME_CANT_BE_EMPTY);
        }
        if (0 > signupRequest.getGender() || signupRequest.getGender() > 2) {
            throw new AppException(ErrorCode.GENDER_SIGNUP_INVALID);
        }
        if (!isValidDob(signupRequest.getDob())) {
            throw new AppException(ErrorCode.DOB_SIGNUP_INVALID);
        }
    }

    public ApiResponse<?> updatePassword(NewPassword newPassword) {
        String username = SecurityUtils.getCurrentUsername();
        Account account = accountRepository.findByUser_Username(username).orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        String password = newPassword.getNewPassword().trim();
        if (newPassword.getNewPassword().length() < 8) {
            throw new AppException(ErrorCode.PASSWORD_TOO_SHORT);
        }
        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), account.getPassword())) {
            throw new AppException(ErrorCode.CURRENT_PASSWORD_NOT_EXACT);
        }
        if (passwordEncoder.matches(password, account.getPassword())) {
            throw new AppException(ErrorCode.NEWPASSWORD_CANT_SAME_OLD_PASSWORD);
        }
        account.setPassword(passwordEncoder.encode(password));
        return ApiResponse.builder()
                .data(accountMapper.toResponse(accountRepository.save(account)))
                .message("change password successfully")
                .build();
    }

//    public void initRoles() {
//        if (roleRepository.findByName(Const.ROLE_ADMIN) == null) {
//            roleRepository.saveAndFlush(new Role(Const.ROLE_ADMIN));
////            initAccountAdmin(role);
//        }
//        if (roleRepository.findByName(Const.ROLE_USER) == null) {
//            roleRepository.save(new Role(Const.ROLE_USER));
//        }
//    }

//    private void initAccountAdmin(Role role) {
//        Account account = Account.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("admin"))
//                .isActive(true)
//                .user(new User())
//                .role(role)
//                .build();
//        accountRepository.save(account);
//    }
}
