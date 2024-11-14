package com.global.project.modal;

import com.global.project.entity.Role;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
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
    private Long idRole;
}
