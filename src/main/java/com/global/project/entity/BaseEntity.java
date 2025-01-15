package com.global.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@MappedSuperclass
public class BaseEntity {
    @Column(name = "create_date")
    private LocalDateTime createdAt;

    @Column(name = "create_By")
    private String createdBy;
    @Column(name = "modifier_date")
    private LocalDateTime updatedAt;

    @Column(name = "modifier_by")
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            this.createdBy = userDetails.getUsername();
            this.updatedBy = userDetails.getUsername();
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        } catch (Exception e) {
            this.createdBy = "Unknow User";
            this.updatedBy = "Unknow User";
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }

    }

    @PreUpdate
    public void preUpdate() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            this.updatedBy = userDetails.getUsername();
            this.updatedAt = LocalDateTime.now();
        } catch (Exception e) {
            this.updatedBy = "Unknow User";
            this.updatedAt = LocalDateTime.now();
        }
    }

}
