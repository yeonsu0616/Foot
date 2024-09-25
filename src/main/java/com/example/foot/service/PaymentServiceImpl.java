package com.example.foot.service;

import com.example.foot.constant.PaymentStatus;
import com.example.foot.dto.*;
import com.example.foot.entity.*;
import com.example.foot.repository.*;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;
    private final ItemImgRepository itemImgRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    @Override
    public RequestPayDto findRequestDto(Long id) {
       Order order = orderRepository.findOrderAndPaymentAndMember(id)
               .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

       return RequestPayDto.builder()
               .buyerName(order.getMember().getName())
               .buyerEmail(order.getMember().getEmail())
               .buyerAddress(order.getMember().getAddress())
               .orderPrice(order.getOrderPrice())
               .itemNm(order.getItemNm())
               .orderUid(order.getOrderUid())
               .buyerTel(order.getMember().getTel())
               .playTime(order.getPlayTime())
               .playDate(order.getPlayDate())
               .level(order.getLevel())
               .gender(order.getGender())
               .matchInfo(order.getMatchInfo())
               .build();
    }

    @Override
    public IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request) {
        try{
            //결제 단건 조회(아임포트)
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getPaymentUid());
            //주문내역 조회
            Order order = orderRepository.findOrderAndPayment(request.getOrderUid())
                    .orElseThrow(()-> new IllegalArgumentException("주문 내역이 없습니다."));

            //결제 완료가 아니면
            if(!iamportResponse.getResponse().getStatus().equals("paid")){
                //주문,결제 삭제
                orderRepository.delete(order);
                paymentRepository.delete(order.getPayment());

                throw new RuntimeException("결제 미완료");
            }
            //DB에 저장된 결제 금액
            int orderPrice = order.getOrderPrice();
            // 실 결제 금액
            int iamportPrice = iamportResponse.getResponse().getAmount().intValue();
            //결제 금액 검증
            if(iamportPrice != orderPrice){
                //주문,결제 삭제
                orderRepository.delete(order);
                paymentRepository.delete(order.getPayment());

                //결제금액 위변조로 의심되는 결제금액을 취소(아임포트)
                iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(),true,new BigDecimal(iamportPrice)));

                throw new RuntimeException("결제금액 위변조 의심");
            }
            //결제 상태 변경
            order.getPayment().changePaymentBySuccess(PaymentStatus.OK,iamportResponse.getResponse().getImpUid());

            return iamportResponse;

        }catch (IamportResponseException e){
            throw new RuntimeException(e);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        System.out.println("이메일"+email);
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);
        System.out.println("가격"+orders.getFirst().getOrderPrice());
        List<OrderHistDto> orderHistDtos = new ArrayList<>();
        // Order -> OrderHistDto
        // OrderItem -> OrderItemDto
        for(Order order : orders){// 주문리스트 -> 주문
            // 주문 히스토리 객체 생성시 매개변수 -> 주문 // 객체 생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            // 주문에 있는 주문 아이템 리스트를 추출
            List<OrderItem> orderItems = order.getOrderItems();
            //주문 아이템 리스트에서 -> 주문 아이템
            for(OrderItem orderItem : orderItems){
                // 주문 아이템에 있는 item 을 추출하고 item id 를 매개변수로 입력
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(
                        // itemImg 객체를 추출 -> 조건 "Y" 대표 이미지 ItemImg
                        orderItem.getItem().getId(), "Y");
                // OrderItemDto 객체를 생성 -> 매개변수 orderItem 객체, itemImg -> url
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, orderItem.getPlayDate(),orderItem.getPlayTime(), itemImg.getImgUrl(), orderItem.getPlayAddress());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();
        System.out.println(email+"fffffff");
        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){
            return cartDetailDtoList;
        }
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

}
