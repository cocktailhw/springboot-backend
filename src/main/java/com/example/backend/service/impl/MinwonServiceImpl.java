package com.example.backend.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.Member;
import com.example.backend.domain.Minwon;
import com.example.backend.domain.MinwonStatus;
import com.example.backend.repository.MemberRepository;
import com.example.backend.repository.MinwonRepository;
import com.example.backend.service.MinwonService;
import com.example.backend.vo.MinwonRequest;
import com.example.backend.vo.MinwonResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MinwonServiceImpl implements MinwonService {

    private final MinwonRepository minwonRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MinwonResponse applyMinwon(MinwonRequest request) {
        Member member = getCurrentMember();

        Minwon minwon = Minwon.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .status(MinwonStatus.WAITING)
                .build();

        return MinwonResponse.from(minwonRepository.save(minwon));
    }

    @Override
    public List<MinwonResponse> getMyMinwons() {
        Member member = getCurrentMember();
        return minwonRepository.findByMemberOrderByCreatedAtDesc(member).stream()
                .map(MinwonResponse::from)
                .toList();
    }

    @Override
    public Page<MinwonResponse> getAllMinwons(Pageable pageable) {
        return minwonRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(MinwonResponse::from);
    }

    @Override
    @Transactional
    public MinwonResponse updateMinwonStatus(Long id, MinwonStatus status) {
        Minwon minwon = minwonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("민원을 찾을 수 없습니다."));
        minwon.updateStatus(status);
        return MinwonResponse.from(minwon);
    }

    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        return memberRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));
    }
}
