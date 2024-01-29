package com.ssafy.lam.member.model.service;

import com.ssafy.lam.dto.Member;
import com.ssafy.lam.member.model.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService{
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        return memberRepository.findByMemberId(memberId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(Member member) {
        return User.builder()
                .username(member.getMemberId())
                .password(passwordEncoder.encode(member.getPassword()))
                .roles(member.getRoles().stream().toArray(String[]::new))
                .build();
    }
}
