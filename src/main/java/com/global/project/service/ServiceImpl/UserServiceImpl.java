package com.global.project.service.ServiceImpl;

import com.global.project.dto.UserDto;
import com.global.project.entity.Customer;
import com.global.project.repository.CustomerRepository;
import com.global.project.repository.UserRepository;
import com.global.project.service.IUserService;
import com.global.project.configuration.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Transactional
public class UserServiceImpl implements IUserService, UserDetailsService {
    @Autowired
    IUserService iUserService;
    @Autowired
    UserRepository userRepository;

//    @Autowired
//    public UserServiceImpl(UserRepository userRepository,
//                           CustomerRepository customerRepository) {
//        this.userRepository = userRepository;
//        this.customerRepository = customerRepository;
////        Random random = new Random();
////
////        for (int i = 0; i < 1000; i++) {
////            Customer customer = new Customer();
////            customer.setName(generateRandomName());
////            customer.setAge(random.nextInt(80) + 18); // Random age between 18 and 97
////            customerRepository.save(customer);
////        }
//    }
////    private String generateRandomName() {
////        String[] names = {"Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Hank", "Ivy", "Jack"};
////        Random random = new Random();
////        int index = random.nextInt(names.length);
////        return names[index];
////    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserDetailsImpl(userRepository.findByUsername(username).get());
    }

    @Override
    public UserDto signup(UserDto dto) {
        return null;
    }
}
