package com.global.project.repository;

import com.global.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @Query("select u from User u where u.email = ?1")
    User getByUsername(String username);
    @Query("select u from User u where u.username like %?1% and u.role.name <> 'ROLE_SYSTEM'")
    Page<User> searchUserByUserName(String textSearch, Pageable page);
    @Query("select u from User u where u.role.name <> 'ROLE_SYSTEM'")
    Page<User> findByPage(Pageable page);
}
