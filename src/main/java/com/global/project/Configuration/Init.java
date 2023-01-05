package com.global.project.Configuration;

import com.global.project.Entity.RoleEntity;
import com.global.project.Entity.UserEntity;
import com.global.project.Repository.RoleRepository;
import com.global.project.Repository.UserRepository;
import com.global.project.Utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping(value = "/public")
public class Init {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping(value = "/init")
    public String init(){
        UserEntity checkExsit = userRepository.findByUsername("admin").orElse(null);
        if(checkExsit == null){
            UserEntity user = new UserEntity();
            user.setEmail("admin@gmail.com");
            user.setUsername("admin");
            user.setActive(true);
            user.setPhone("0000000000");
            user.setAddress("admin");
            user.setBirthDate(new Date());
            user.setAge(0);
            RoleEntity role = roleRepository.findByName(Const.ROLE_SYSTEM);
            if(role == null){
                role = new RoleEntity();
                role.setName(Const.ROLE_SYSTEM);
                roleRepository.saveAndFlush(role);
            }
            user.setRole(role);
            user.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(user);
        }
        RoleEntity roleAdmin = roleRepository.findByName(Const.ROLE_ADMIN);
        if(roleAdmin == null){
            roleAdmin = new RoleEntity();
            roleAdmin.setName(Const.ROLE_ADMIN);
            roleRepository.saveAndFlush(roleAdmin);
        }
        RoleEntity roleUser = roleRepository.findByName(Const.ROLE_USER);
        if(roleUser == null){
            roleUser = new RoleEntity();
            roleUser.setName(Const.ROLE_USER);
            roleRepository.saveAndFlush(roleUser);
        }
        return "success";
    }
}
