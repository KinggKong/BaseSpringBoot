package com.global.project.Repository;

import com.global.project.Entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    @Query(value = "select r from RoleEntity r where r.name like ?1")
    RoleEntity findByName(String name);
}
