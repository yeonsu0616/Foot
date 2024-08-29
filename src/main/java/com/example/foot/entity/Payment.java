package com.example.foot.entity;

import com.example.foot.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int price;
    private PaymentStatus status;
    private String paymentUid; //결제고유번호
    private String playDate;
    private LocalTime playTime;
    private String level;
    private String gender;
    private String matchInfo;
    private String playAddress;

    @ManyToOne
    @JoinColumn(name = "item_id") // Foreign key 이름 지정
    private Item item;

//    private String playDate;
//
//
//   private LocalTime playTime;

    @Builder
    public Payment(int price, PaymentStatus status,Item item){
        this.price = price;
        this.status = status;
        this.playDate = item.getPlayDate();
        this.playTime = item.getPlayTime();
        this.level = item.getLevel();
        this.gender = item.getGender();
        this.matchInfo = item.getMatchInfo();
        this.playAddress = item.getPlayAddress();


    }
    public void changePaymentBySuccess(PaymentStatus status,String paymentUid){
        this.status = status;
        this.paymentUid = paymentUid;
    }


}
