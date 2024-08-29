package com.example.foot.service;

import com.example.foot.constant.PaymentStatus;
import com.example.foot.dto.OrderDto;
import com.example.foot.dto.OrderHistDto;
import com.example.foot.dto.OrderItemDto;
import com.example.foot.entity.*;
import com.example.foot.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;
    private final PaymentRepository paymentRepository;

    //일반결제
    public Long order(OrderDto orderDto, String email) {
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();

        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount(),item.getPrice(), item.getPlayDate(), item.getPlayTime(),
                item.getLevel(), item.getGender(), item.getMatchInfo(), item.getPlayAddress());

        orderItemList.add(orderItem);
        Payment payment = new Payment();

        Order order = Order.createOrder(member, orderItemList,payment, item.getPlayDate(), item.getPlayTime(), item.getLevel(), item.getGender(), item.getMatchInfo(),item.getPlayAddress());

        order.setItemNm(item.getItemNm());
        order.setOrderPrice(orderItem.getTotalPrice());
        order.setPlayTime(item.getPlayTime());
        order.setPlayDate(item.getPlayDate());
        order.setLevel(item.getLevel());
        order.setGender(item.getGender());
        order.setMatchInfo(item.getMatchInfo());
        order.setPlayAddress(item.getPlayAddress());
        //payment
        payment.setPrice(order.getOrderPrice());
        payment.setPrice(item.getPrice());
        payment.setPaymentUid(order.getOrderUid());
        payment.setStatus(PaymentStatus.OK);
        payment.setPlayDate(item.getPlayDate());
        payment.setLevel(item.getLevel());
        payment.setGender(item.getGender());
        payment.setMatchInfo(item.getMatchInfo());
        payment.setPlayTime(item.getPlayTime());
        payment.setPlayAddress(item.getPlayAddress());
        paymentRepository.save(payment);
        orderRepository.save(order);
        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        System.out.println("이메일"+email);

        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);
//        System.out.println("가격"+orders.getFirst().getOrderPrice());
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

    @Transactional(readOnly = true) //결제완료창에서 쓸거임
    public OrderHistDto getPaymentOrderList(Long id){
        Order order = orderRepository.findOrder(id);
        // Order -> OrderHistDto
        // OrderItem -> OrderItemDto
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
                OrderItemDto orderItemDto = new OrderItemDto(orderItem,  orderItem.getPlayDate(),orderItem.getPlayTime(), itemImg.getImgUrl(), orderItem.getPlayAddress());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
        return orderHistDto;
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }
        return  true;
    }
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }
  //장바구니 결제
    public Long orders(List<OrderDto> orderDtoList, String email){
        //Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        //Member 엔티티 객체 추출
        Member member = memberRepository.findByEmail(email);
        //주문 Item 리스트 객체 생성
        List<OrderItem> orderItemList = new ArrayList<>();
        //주문 Dto List에 있는 객체만큼 반복
        for(OrderDto orderDto : orderDtoList){
            //주문 -> Item Entity 객체 추출
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
            System.out.println("999"+item.getPrice());
            System.out.println("장바구니 이름"+item.getItemNm());
            // 주문 Item 생성
            OrderItem orderItem = OrderItem.createOrderItem(item,orderDto.getCount(),item.getPrice(),item.getPlayDate(),item.getPlayTime(),
                    item.getLevel(), item.getGender(), item.getMatchInfo(), item.getPlayAddress());
            orderItem.setOrderPrice(item.getPrice());
            orderItem.setOrderPrice(orderItem.getTotalPrice());
            // 주문 Item List에 추가
            orderItemList.add(orderItem);
            System.out.println("오더 리스트 가격 : "+ orderDtoList.getFirst().getPrice());
            System.out.println("오더 아이템 가격"+orderItem.getOrderPrice());
            System.out.println("토탈 가격 : "+ orderItem.getTotalPrice());

        }

        Payment payment = new Payment();
        ////////// 주문 Item List가 완성/////////////////////
        // 주문Item리스트, Member를 매개변수로 넣고
        // 주문서 생성
        Order order = Order.createOrder(member, orderItemList,payment, payment.getPlayDate(), payment.getPlayTime(), payment.getLevel(), payment.getGender(), payment.getMatchInfo(), payment.getPlayAddress());

        System.out.println(order.getOrderPrice()+"11111111");
        // 주문서 저장
        order.setItemNm(orderItemList.get(0).getItem().getItemNm());
        System.out.println("오더 아이템 리스트 :" + orderItemList.get(0).getItem().getItemNm());
        order.setOrderPrice(order.getTotalPrice());
        System.out.println("오더 겟 :"+order.getTotalPrice());
        payment.setPaymentUid(order.getOrderUid());
        payment.setPrice(order.getTotalPrice());
        payment.setPlayDate(order.getPlayDate());
        payment.setPlayTime(order.getPlayTime());
        payment.setPlayAddress(order.getPlayAddress());
        payment.setStatus(PaymentStatus.OK);
        paymentRepository.save(payment);
        orderRepository.save(order);

        return order.getId();
    }
}
