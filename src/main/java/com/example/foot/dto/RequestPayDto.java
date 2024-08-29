package com.example.foot.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RequestPayDto {

    private String itemNm;
    private String buyerName;
    private int orderPrice;
    private String buyerEmail;
    private String buyerAddress;
    private String orderUid;
    private String buyerTel;
    private String playDate;
    private LocalTime playTime;
    private String level;
    private String gender;
    private String matchInfo;
    private String playAddress;


    @Builder
    public RequestPayDto(String itemNm, String buyerName, int orderPrice, String buyerEmail, String buyerAddress, String orderUid,String buyerTel,
                         String playDate,LocalTime playTime,String level,String gender,String matchInfo, String playAddress) {

        this.itemNm = itemNm;
        this.buyerName = buyerName;
        this.orderPrice = orderPrice;
        this.buyerEmail = buyerEmail;
        this.buyerAddress = buyerAddress;
        this.orderUid = orderUid;
        this.buyerTel = buyerTel;
        this.playDate = playDate;
        this.playTime = playTime;
        this.level = level;
        this.gender = gender;
        this.matchInfo = matchInfo;
        this.playAddress = playAddress;
    }

}
