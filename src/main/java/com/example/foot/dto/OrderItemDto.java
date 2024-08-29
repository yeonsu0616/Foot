package com.example.foot.dto;

import com.example.foot.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class OrderItemDto {
    private String itemNm;
    private int count;
    private int orderPrice;
    private LocalTime playTime;
    private String playDate;
    private String playAddress;
    private String imgUrl;
    public OrderItemDto(OrderItem orderItem, String playDate,LocalTime playTime,String imgUrl,String playAddress){
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.playDate=orderItem.getPlayDate();
        this.playTime=orderItem.getPlayTime();
        this.imgUrl = imgUrl;
        this.playAddress= orderItem.getPlayAddress();
    }
}
