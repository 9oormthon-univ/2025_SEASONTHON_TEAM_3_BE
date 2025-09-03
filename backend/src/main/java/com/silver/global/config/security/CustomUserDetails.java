package com.silver.global.config.security;


import java.util.ArrayList;
import java.util.Collection;

import com.silver.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private User member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        String roles = member.getRole().toString();
        for(String role : roles.split(",")) {
            authorities.add(() -> role);
        }
        return authorities;
    }

    @Override
    public String getPassword() {return member.getPassword();}

    @Override
    public String getUsername() {return member.getEmail();}


    public Long getId() {return member.getId();} // Member 엔티티의 ID를 반환}

    @Override
    public boolean isAccountNonExpired() {return true;}

    @Override
    public boolean isAccountNonLocked() {return true;}

    @Override
    public boolean isCredentialsNonExpired() {return true;}

    @Override
    public boolean isEnabled() {return true;}
}