package com.global.project.Repository;

import com.global.project.Entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    @Query(value = "select f.name from FileEntity f where f.name like ?1")
    String findFileByName(String name);
}
