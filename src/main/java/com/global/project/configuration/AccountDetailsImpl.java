package com.global.project.configuration;

import com.global.project.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@Transactional
public class AccountDetailsImpl implements UserDetails {
    Account account;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(account.getRole().getName()));
        return roles;
    }

    public Account getAccount() {
        return this.account;
    }

    @Override
    public String getPassword() {
        if (account == null)
            return null;
        if (account.getPassword() == null)
            return null;
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        if (account == null)
            return null;
        if (account.getUser() == null)
            return null;
        return account.getUser().getUsername();
    }

    public String getRoleName() {
        return account.getRole().getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
