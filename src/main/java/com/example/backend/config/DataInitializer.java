package com.example.backend.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.Member;
import com.example.backend.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 기본 관리자/일반 계정이 DB에 없을 경우 기동 시 생성한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createMemberIfAbsent("admin", "admin1234!", "ROLE_ADMIN");
        createMemberIfAbsent("user", "user1234!", "ROLE_USER");
    }

    private void createMemberIfAbsent(String username, String rawPassword, String role) {
        if (memberRepository.existsByUsername(username)) {
            return;
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();

        memberRepository.save(member);
        log.info("[DataInitializer] 기본 계정 생성: username={}, role={}", username, role);
    }
}
