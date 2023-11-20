package com.global.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "tbl_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity{
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
    private Role role;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
