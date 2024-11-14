package com.global.project.mapper;

import com.global.project.dto.UserDto;
import com.global.project.entity.User;
import com.global.project.modal.UserRequest;
import com.global.project.utils.Const;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserMapper {
    public User toUser(UserRequest userRequest) {
        return User.builder()
                .age(userRequest.getAge())
                .active(userRequest.getActive())
                .address(userRequest.getAddress())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .gender(userRequest.getGender())
                .avatar(userRequest.getAvatar())
                .fullName(userRequest.getFullName())
                .birthDate(userRequest.getBirthDate())
                .password(userRequest.getPassword())
                .username(userRequest.getUsername())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .phone(user.getPhone())
                .createdDate(user.getCreateDate())
                .roleName(user.getRole().getName())
                .gender(user.getGender())
                .avatar(Const.DOMAIN + user.getAvatar())
                .fullName(user.getFullName())
                .age(user.getAge())
                .birthDate(user.getBirthDate())
                .build();
    }

    public List<UserDto> toListDto(List<User> users) {
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream().map(this::toUserDto).toList();
    }
}
