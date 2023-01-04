package com.global.project.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String fullName;
    private String avatar;
    private Integer age;
    private String address;
    private Boolean active;
    private Integer gender;
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    @ManyToOne(fetch = FetchType.EAGER)
    private RoleEntity role;

    public UserEntity(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
