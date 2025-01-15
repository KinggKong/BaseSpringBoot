package com.global.project.repository;

import com.global.project.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByAccountIdAndDeviceInfo(Long accountId, String deviceInfo);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken getByToken(String token);

    //String deleteRefreshToken(Optional<RefreshToken> token);
}
