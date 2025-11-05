package com.example.zzserver.member.service.auth;


import com.example.zzserver.config.dto.CustomUserDetails;
import com.example.zzserver.member.entity.Members;
import com.example.zzserver.member.repository.jpa.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Members member = memberRepository.findByEmail(username);

        return new CustomUserDetails(member);
    }
}