package com.global.project.dto;

import com.global.project.entity.User;
import com.global.project.utils.Const;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private Integer age;
    private Integer gender;
    private String address;
    private Date birthDate;
    private Boolean isActive;
    private Date createdDate;
    private String roleName;
    private String phone;
    private String avatar;

}
