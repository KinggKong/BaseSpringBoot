package com.global.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

@MappedSuperclass
public class BaseEntity {
    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "create_By")
    private String createBy;
    @Column(name = "modifier_date")
    private Date modifierDate;

    @Column(name = "modifier_by")
    private String modifierBy;

    @PrePersist
    public void prePersist(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            this.createBy = userDetails.getUsername();
            this.modifierBy = userDetails.getUsername();
            this.createDate = new Date();
            this.modifierDate = new Date();
        }catch (Exception e){
            this.createBy ="Unknow User";
            this.modifierBy ="Unknow User";
            this.createDate = new Date();
            this.modifierDate = new Date();
        }

    }

    @PreUpdate
    public void preUpdate(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            this.modifierBy = userDetails.getUsername();
            this.modifierDate = new Date();
        }catch (Exception e){
            this.modifierBy ="Unknow User";
            this.modifierDate = new Date();
        }
    }


    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getModifierDate() {
        return modifierDate;
    }

    public void setModifierDate(Date modifierDate) {
        this.modifierDate = modifierDate;
    }

    public String getModifierBy() {
        return modifierBy;
    }

    public void setModifierBy(String modifierBy) {
        this.modifierBy = modifierBy;
    }
}
