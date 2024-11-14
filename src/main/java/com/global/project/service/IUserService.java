package com.global.project.service;

import com.global.project.dto.UserDto;
import com.global.project.modal.UserRequest;

import java.util.List;

public interface IUserService {
    UserDto signup(UserRequest userRequest);
    List<UserDto> findAll();
    UserDto findById(Long id);
}
