package com.example.foot.repository;

import com.example.foot.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
    Optional<Member> findByTel(String tel);
    Optional<Member> findById(Long id);
}
