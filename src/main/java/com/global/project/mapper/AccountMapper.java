package com.global.project.mapper;

import com.global.project.dto.AccountResponse;
import com.global.project.entity.Account;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AccountMapper {
    public AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUser().getUsername())
                .email(account.getEmail())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .role(account.getRole().getName())
                .isActive(account.getIsActive())
                .build();
    }

    public List<AccountResponse> toListResponse(List<Account> accounts) {
        if (accounts == null) return Collections.emptyList();
        return accounts.stream().map(this::toResponse).toList();
    }
}
