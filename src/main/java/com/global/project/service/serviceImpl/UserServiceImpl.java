package com.global.project.service.serviceImpl;

import com.global.project.dto.UserDto;
import com.global.project.entity.Role;
import com.global.project.entity.User;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        User checkExsit = userRepository.findByUsername("admin").orElse(null);
        if(checkExsit == null){
            User user = new User();
            user.setEmail("admin");
            user.setUsername("admin");
            user.setActive(true);
            user.setPhone("0000000000");
            user.setAddress("admin");
            user.setBirthDate(new Date());
            user.setAge(0);
            Role role = roleRepository.findByName(Const.ROLE_ADMIN);
            if(role == null){
                role = new Role();
                role.setName(Const.ROLE_ADMIN);
                roleRepository.saveAndFlush(role);
            }
            user.setRole(role);
            user.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(user);
        }
        Role roleAdmin = roleRepository.findByName(Const.ROLE_ADMIN);
        if(roleAdmin == null){
            roleAdmin = new Role();
            roleAdmin.setName(Const.ROLE_ADMIN);
            roleRepository.saveAndFlush(roleAdmin);
        }
        Role roleUser = roleRepository.findByName(Const.ROLE_USER);
        if(roleUser == null){
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
    public UserDto signup(UserDto dto) {
        return null;
    }
}
