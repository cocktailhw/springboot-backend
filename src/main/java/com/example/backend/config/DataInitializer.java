package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.backend.domain.Member;
import com.example.backend.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 기본 관리자/일반 계정이 DB에 없을 경우 기동 시 생성한다.
 * 비밀번호는 K8s Secret 등에서 주입된 BCrypt 해시를 그대로 저장한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;

    @Value("${auth.admin.password-hash}")
    private String adminPasswordHash;

    @Value("${auth.user.password-hash}")
    private String userPasswordHash;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createMemberIfAbsent("admin", adminPasswordHash, "ROLE_ADMIN");
        createMemberIfAbsent("user", userPasswordHash, "ROLE_USER");
    }

    private void createMemberIfAbsent(String username, String passwordHash, String role) {
        if (memberRepository.existsByUsername(username)) {
            return;
        }
        if (!StringUtils.hasText(passwordHash)) {
            throw new IllegalStateException(
                    "기본 계정 '" + username + "' 생성에 필요한 password-hash 환경변수가 없습니다.");
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordHash)
                .role(role)
                .build();

        memberRepository.save(member);
        log.info("[DataInitializer] 기본 계정 생성: username={}, role={}", username, role);
    }
}
