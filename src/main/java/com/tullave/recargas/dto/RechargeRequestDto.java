package com.tullave.recargas.dto;

import java.math.BigDecimal;

import com.tullave.recargas.model.PaymentMethod;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeRequestDto {

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(regexp = "^\\d{16}$", message = "El número de tarjeta debe contener exactamente 16 dígitos")
    private String cardNumber;

    @NotNull(message = "El monto de la recarga es obligatorio")
    @DecimalMin(value = "2000.00", message = "La recarga mínima permitida es de $2.000")
    @DecimalMax(value = "200000.00", message = "La recarga máxima permitida es de $200.000")
    private BigDecimal amount;

    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethod paymentMethod;
}
