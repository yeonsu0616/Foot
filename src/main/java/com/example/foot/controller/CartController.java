package com.example.foot.controller;

import com.example.foot.dto.CartDetailDto;
import com.example.foot.dto.CartItemDto;
import com.example.foot.dto.CartOrderDto;
import com.example.foot.dto.SessionUser;
import com.example.foot.entity.CartItem;
import com.example.foot.entity.Item;
import com.example.foot.entity.Member;
import com.example.foot.entity.Order;
import com.example.foot.repository.CartItemRepository;
import com.example.foot.repository.CartRepository;
import com.example.foot.repository.ItemRepository;
import com.example.foot.repository.OrderRepository;
import com.example.foot.service.CartService;
import com.example.foot.service.MemberService;
import com.example.foot.service.OrderService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final HttpSession httpSession;
    private final MemberService memberService;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;



    @PostMapping(value = "/cart")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                         BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        String email;
        if(httpSession.getAttribute("user") != null){
            System.out.println("aaaaa");
            email = ((SessionUser)httpSession.getAttribute("user")).getEmail();
        }
        else{
            System.out.println("bbbbb");
            email = principal.getName();
        }
        Long cartItemId;
        List<String>result = new ArrayList<>();
        try {
            cartItemId = cartService.addCart(cartItemDto, email);
            Member m = memberService.findByEmail(email);
            result.add(Long.toString(cartItemId));
            result.add(m.getName());
            result.add(m.getEmail());
            result.add(m.getAddress());
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){

        List<CartDetailDto> cartDetailDtoList;
        if(httpSession.getAttribute("user") != null){
            System.out.println("aaaaa");
            String email = ((SessionUser)httpSession.getAttribute("user")).getEmail();
            cartDetailDtoList =  cartService.getCartList(email);
        }
        else{
            System.out.println("bbbbb");
            cartDetailDtoList = cartService.getCartList(principal.getName());
        }

        model.addAttribute("cartItems",cartDetailDtoList);
        return "cart/cartList";
    }
    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       int count, Principal principal) {
        System.out.println(cartItemId);
        String email;
        if(httpSession.getAttribute("user") != null){
            System.out.println("aaaaa");
            email = ((SessionUser)httpSession.getAttribute("user")).getEmail();
        }
        else{
            System.out.println("bbbbb");
            email = principal.getName();
        }
        if (count <= 0) {
            return new ResponseEntity<String>("최소 1개이상 담아주세요.", HttpStatus.BAD_REQUEST);
        } else if (!cartService.validateCartItem(cartItemId, email)) {
            return new ResponseEntity<String>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        List<String>result = new ArrayList<>();
        try {
        cartService.updateCartItemCount(cartItemId, count);
            Member m = memberService.findByEmail(email);
            result.add(Long.toString(cartItemId));
            result.add(m.getName());
            result.add(m.getEmail());
            result.add(m.getAddress());
        }
        catch (Exception e){ // 실패하면
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<List<String>>(result, HttpStatus.OK);
    }

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       Principal principal){
        String email;
        if(httpSession.getAttribute("user") != null){
            System.out.println("aaaaa");
            email = ((SessionUser)httpSession.getAttribute("user")).getEmail();
        }
        else{
            System.out.println("bbbbb");
            email = principal.getName();
        }
        if (!cartService.validateCartItem(cartItemId, email)) {
            return new ResponseEntity<String>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        Long orderId;
        List<String>result = new ArrayList<>();
        try {
        cartService.deleteCartItem(cartItemId);
            Member m = memberService.findByEmail(email);
            result.add(Long.toString(cartItemId));
            result.add(m.getName());
            result.add(m.getEmail());
            result.add(m.getAddress());
        }catch (Exception e){ // 실패하면
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<List<String>>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/cart/orders")
    public @ResponseBody
    ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto,
                                                      Principal principal){
        System.out.println(cartOrderDto.getCartItemId());
        CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartOrderDtoList().get(0).getCartItemId())
                    .orElseThrow(EntityExistsException::new);
            Item item = itemRepository.findById(cartItem.getItem().getId()).orElseThrow(EntityNotFoundException::new);
            System.out.println(item.getItemNm()+"ddddd");
        //CartOrderDtoList List <- getCartOrderDtoList 통해서 Views 내려온 리스트
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        // null, size가 0이면 실행
        String email;
        if(httpSession.getAttribute("user") != null){
            System.out.println("aaaaa");
            email = ((SessionUser)httpSession.getAttribute("user")).getEmail();
        }
        else{
            System.out.println("bbbbb");
            email = principal.getName();
        }
        System.out.println(email+"123123123");
        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요.",HttpStatus.FORBIDDEN);
        }
        // 전체 유효성검사
        for(CartOrderDto cartOrder : cartOrderDtoList){
            if(!cartService.validateCartItem(cartOrder.getCartItemId(),email)){
                return new ResponseEntity<String>("주문 권한이 없습니다.",HttpStatus.FORBIDDEN);
            }
        }
        Long orderId;
        List<String>result = new ArrayList<>();
        try {
            orderId = cartService.orderCartItem(cartOrderDtoList, email);
            //orderItemId = cartService.orderCartItem(cartOrderDtoList,email);
            System.out.println(orderId+"@@@@@");
            Member m = memberService.findByEmail(email);
            System.out.println(m);
            System.out.println("=======================================");

            result.add(item.getItemNm());
            System.out.println("아이템아이디"+item.getItemNm());
            System.out.println("아이템번호"+orderId);
            Order order=orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
            order.setOrderPrice(item.getPrice());
            order.setItemNm(item.getItemNm());
            System.out.println("오더 가격"+order.getOrderPrice());
        }
        catch (Exception e){ // 실패하면
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        //성공
        return new ResponseEntity<>(orderId,HttpStatus.OK);
    }


}
