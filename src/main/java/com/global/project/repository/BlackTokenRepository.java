package com.global.project.repository;

import com.global.project.entity.BlackToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackTokenRepository extends JpaRepository<BlackToken,Long> {
    boolean existsByToken(String token);
}
