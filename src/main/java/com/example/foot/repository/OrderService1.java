package com.example.foot.repository;

import com.example.foot.entity.Member;
import com.example.foot.entity.Order;

public interface OrderService1 {
    Order autoOrder(Member member); //자동 주문
}
