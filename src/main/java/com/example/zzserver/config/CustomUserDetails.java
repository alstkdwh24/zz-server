package com.example.zzserver.config;

import com.example.zzserver.member.entity.Members;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Slf4j
@Getter
public class CustomUserDetails implements UserDetails {

    private Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);
    private final Members members;

    public CustomUserDetails(Members members) {
        this.members = members;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        List<String> roles = new ArrayList<>();
        roles.add(members.getRole().toString());

        return roles.stream()
                .map(SimpleGrantedAuthority:: new)
                .toList();
    }


    @Override
    public String getPassword() {
        return members.getUserPw();
    }

    @Override
    public String getUsername() {
        return members.getEmail(); // username 필드에 맞게 반환
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
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
