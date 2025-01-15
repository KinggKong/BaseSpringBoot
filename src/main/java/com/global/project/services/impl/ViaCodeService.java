package com.global.project.services.impl;

import com.global.project.entity.ViaCode;
import com.global.project.exception.AppException;
import com.global.project.exception.ErrorCode;
import com.global.project.repository.ViaCodeRepository;
import com.global.project.services.IViaCodeService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ViaCodeService implements IViaCodeService {
    ViaCodeRepository viaRepository;

    @Override
    public boolean validateCode(int code, String email) {
        ViaCode viaCode = viaRepository.findByEmail(email);
        if (viaCode != null) {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(viaCode.getCreatedAt(), now);
            if (duration.toMinutes() > 3) {
               throw new AppException(ErrorCode.VERIFY_CODE_EXPIRED);
            }
            return code ==viaCode.getViaCode();
        }
        return false;
    }
}
