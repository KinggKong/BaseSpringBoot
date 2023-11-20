package com.global.project.repository;

import com.global.project.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    @Query(value = "select f.name from File f where f.name like ?1")
    String findFileByName(String name);
}
