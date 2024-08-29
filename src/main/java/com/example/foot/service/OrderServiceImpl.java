package com.example.foot.service;

import com.example.foot.constant.PaymentStatus;
import com.example.foot.entity.Member;
import com.example.foot.entity.Order;
import com.example.foot.entity.Payment;
import com.example.foot.repository.OrderRepository;
import com.example.foot.repository.OrderService1;
import com.example.foot.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService1 {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public Order autoOrder(Member member) {

        // 임시 결제내역 생성
        Payment payment = Payment.builder()
                .price(1000)
                .status(PaymentStatus.READY)
                .build();

        paymentRepository.save(payment);


        // 주문 생성
        Order order = Order.builder()
                .member(member)
                .orderPrice(1000)
                .itemNm("1달러샵 상품")
                .orderUid(UUID.randomUUID().toString())
                .payment(payment)
                .build();

        return orderRepository.save(order);
    }

}
