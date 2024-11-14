package com.global.project.restController;


import com.global.project.dto.UserDto;
import com.global.project.service.IUserService;
import com.global.project.service.serviceImpl.UserServiceImpl;
import com.global.project.utils.Const;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RestUserController {
    IUserService userService;

    @GetMapping("")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("authentication.principal.user.id == #id or hasRole('ADMIN')")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

}
