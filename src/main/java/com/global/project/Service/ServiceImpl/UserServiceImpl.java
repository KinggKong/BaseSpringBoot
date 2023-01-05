package com.global.project.Service.ServiceImpl;

import com.global.project.Dto.UserDto;
import com.global.project.Repository.UserRepository;
import com.global.project.Service.IUserService;
import com.global.project.Configuration.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements IUserService, UserDetailsService {
    @Autowired
    IUserService iUserService;
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserDetailsImpl(userRepository.findByUsername(username).get());
    }

    @Override
    public UserDto signup(UserDto dto) {
        return null;
    }
}
