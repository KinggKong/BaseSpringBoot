package com.global.project.restController;


import com.global.project.dto.CustomerDto;
import com.global.project.repository.querydslRepository.CustomerQuerydslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/customer")
@RequiredArgsConstructor
public class restCustomerController {
    private final CustomerQuerydslRepository customerQuerydslRepository;
    @GetMapping
    public Page<CustomerDto> findByPage(Pageable page,
                                        @RequestParam(required = false) String textSearch){
        return customerQuerydslRepository.findByPage(page,textSearch);
    }
}
