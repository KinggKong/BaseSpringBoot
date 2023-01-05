package com.global.project.Repository;

import com.global.project.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    @Query("select u from UserEntity u where u.email = ?1")
    UserEntity getByUsername(String username);
    @Query("select u from UserEntity u where u.username like %?1% and u.role.name <> 'ROLE_SYSTEM'")
    Page<UserEntity> searchUserByUserName(String textSearch,Pageable page);
    @Query("select u from UserEntity u where u.role.name <> 'ROLE_SYSTEM'")
    Page<UserEntity> findByPage(Pageable page);
}
