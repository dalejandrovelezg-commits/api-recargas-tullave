package com.tullave.recargas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tullave.recargas.model.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeResponseDto {

    private Long id;
    private String cardNumber;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
}
