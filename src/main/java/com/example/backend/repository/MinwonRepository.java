package com.example.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.domain.Member;
import com.example.backend.domain.Minwon;

public interface MinwonRepository extends JpaRepository<Minwon, Long> {

    @EntityGraph(attributePaths = "member")
    List<Minwon> findByMemberOrderByCreatedAtDesc(Member member);

    @EntityGraph(attributePaths = "member")
    Page<Minwon> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
