package com.global.project.service.serviceImpl;

import com.global.project.dto.UserDto;
import com.global.project.entity.Role;
import com.global.project.entity.User;
import com.global.project.mapper.UserMapper;
import com.global.project.modal.UserRequest;
import com.global.project.repository.RoleRepository;
import com.global.project.repository.UserRepository;
import com.global.project.service.IUserService;
import com.global.project.configuration.UserDetailsImpl;
import com.global.project.utils.Const;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements IUserService, UserDetailsService {
    @Autowired
    IUserService iUserService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        User checkExsit = userRepository.findByUsername("admin").orElse(null);
        if (checkExsit == null) {
            User user = new User();
            user.setEmail("admin");
            user.setUsername("admin");
            user.setActive(true);
            user.setPhone("0000000000");
            user.setAddress("admin");
            user.setBirthDate(new Date());
            user.setAge(0);
            Role role = roleRepository.findByName(Const.ROLE_ADMIN);
            if (role == null) {
                role = new Role();
                role.setName(Const.ROLE_ADMIN);
                roleRepository.saveAndFlush(role);
            }
            user.setRole(role);
            user.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(user);
        }
        Role roleAdmin = roleRepository.findByName(Const.ROLE_ADMIN);
        if (roleAdmin == null) {
            roleAdmin = new Role();
            roleAdmin.setName(Const.ROLE_ADMIN);
            roleRepository.saveAndFlush(roleAdmin);
        }
        Role roleUser = roleRepository.findByName(Const.ROLE_USER);
        if (roleUser == null) {
            roleUser = new Role();
            roleUser.setName(Const.ROLE_USER);
            roleRepository.saveAndFlush(roleUser);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserDetailsImpl(userRepository.findByUsername(username).get());
    }

    @Override
    public UserDto signup(UserRequest userRequest) {
        Role role = roleRepository.findById(userRequest.getIdRole()).orElseThrow(() -> new RuntimeException("Not found role with id: " + userRequest.getIdRole()));
        User user = userMapper.toUser(userRequest);
        user.setRole(role);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        return userMapper.toListDto(users);
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found user with id: " + id));
        return userMapper.toUserDto(user);
    }


}
