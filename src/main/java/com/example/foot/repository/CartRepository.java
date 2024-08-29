package com.example.foot.repository;

import com.example.foot.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Cart findByMemberId(Long memberId);
}
