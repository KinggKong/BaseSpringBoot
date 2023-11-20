package com.global.project.configuration;

import com.global.project.entity.Role;
import com.global.project.entity.User;
import com.global.project.repository.RoleRepository;
import com.global.project.repository.UserRepository;
import com.global.project.utils.Const;
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
        User checkExsit = userRepository.findByUsername("admin").orElse(null);
        if(checkExsit == null){
            User user = new User();
            user.setEmail("admin@gmail.com");
            user.setUsername("admin");
            user.setActive(true);
            user.setPhone("0000000000");
            user.setAddress("admin");
            user.setBirthDate(new Date());
            user.setAge(0);
            Role role = roleRepository.findByName(Const.ROLE_SYSTEM);
            if(role == null){
                role = new Role();
                role.setName(Const.ROLE_SYSTEM);
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
        return "success";
    }
}
