package com.example.foot.controller;

import com.example.foot.dto.*;
import com.example.foot.entity.Order;
import com.example.foot.service.CartService;
import com.example.foot.service.OrderService;
import com.example.foot.service.PaymentService;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final HttpSession httpSession;
    private final OrderService orderService;
    private final CartService cartService;

    @GetMapping("/payment/{id}")
    public String paymentPage(@PathVariable(name = "id", required = false) Long id,
                              Model model, Pageable pageable, Principal principal) {

        RequestPayDto requestPayDto = paymentService.findRequestDto(id);
        Page<OrderHistDto> orderHistDtoList;
        List<CartDetailDto> cartDetailDtoList;
        List<Order> orders ;
        OrderHistDto orderHistDto = orderService.getPaymentOrderList(id);
        if(httpSession.getAttribute("user") != null){
            System.out.println("aaaaa");

            String email = ((SessionUser)httpSession.getAttribute("user")).getEmail();
            System.out.println("이메일"+email);
            System.out.println(pageable);
            orderHistDtoList = orderService.getOrderList(email,pageable);
            cartDetailDtoList =  cartService.getCartList(email);
        }
        else {
            System.out.println("bbbbb");
            orderHistDtoList = orderService.getOrderList(principal.getName(),pageable);
            cartDetailDtoList = cartService.getCartList(principal.getName());
        }
        System.out.println("주문 가격     +++++++++++"+requestPayDto.getOrderPrice());
        System.out.println("아이디"+requestPayDto.getOrderUid());
        System.out.println("이름"+requestPayDto.getItemNm());
        System.out.println("전화번호"+requestPayDto.getBuyerTel());
        System.out.println("Date"+ requestPayDto.getPlayDate());
        System.out.println("level" +requestPayDto.getLevel());
        System.out.println("gender" +requestPayDto.getGender());
        System.out.println("matchInfo" +requestPayDto.getMatchInfo());


        //System.out.println("카트 상품:"+cartDetailDtoList.get(0).getItemNm());
        model.addAttribute("orderItems", orderHistDto.getOrderItemDtoList());
        model.addAttribute("requestDto", requestPayDto);
        model.addAttribute("orders",orderHistDtoList);
        model.addAttribute("cartItems",cartDetailDtoList);
        return "pay/payment"; //
    }

    @ResponseBody
    @PostMapping("/payment")
    public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody PaymentCallbackRequest request) {
        IamportResponse<Payment> iamportResponse = paymentService.paymentByCallback(request);

        log.info("결제 응답={}", iamportResponse.getResponse().toString());

        return new ResponseEntity<>(iamportResponse, HttpStatus.OK);
    }

    @GetMapping("/success-payment")
    public String successPaymentPage() {
        return "pay/success-payment";
    }

    @GetMapping("/fail-payment")
    public String failPaymentPage() {
        return "redirect:/";
    }

}
