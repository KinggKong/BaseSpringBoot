package com.global.project.repository;

import com.global.project.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUser_Username(String username);

    boolean existsByEmail(String email);

    @Query(value = "select acc from Account acc where acc.email = :key or acc.user.phoneNumber = :key or acc.username = :key")
    Optional<Account> findByEmailOrPhoneNumberOrUsername(@Param("key") String key);

    Optional<Account> findByEmail(String email);

    @Query("SELECT a FROM Account a WHERE a.username = :username")
    Optional<Account> findByUsername(@Param("username") String username);

    @Query(value = "select ac from Account  ac where ac.email = :email and ac.isActive = true")
    Optional<Account> checkIsActive(String email);
}
