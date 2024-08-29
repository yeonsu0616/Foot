package com.example.foot.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentCallbackRequest {
    private String paymentUid; //결제 고유 번호
    private String  orderUid; //주문 고유 번호
}
