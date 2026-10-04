package com.tullave.recargas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.tullave.recargas.dto.RechargeRequestDto;
import com.tullave.recargas.dto.RechargeResponseDto;
import com.tullave.recargas.exception.NotFoundException;
import com.tullave.recargas.model.PaymentMethod;
import com.tullave.recargas.model.Recharge;
import com.tullave.recargas.repository.RechargeRepository;
import com.tullave.recargas.service.RechargeServiceImpl;

@ExtendWith(MockitoExtension.class)
class RechargeServiceTests {

    @Mock
    private RechargeRepository rechargeRepository;

    @InjectMocks
    private RechargeServiceImpl rechargeService;

    @Test
    @DisplayName("Obtener recargas por número de tarjeta")
    void testGetRechargesByCardNumber() {
        String cardNumber = "1234567890123456";
        Recharge recharge1 = new Recharge(1L, cardNumber, new BigDecimal("5000.00"), PaymentMethod.CREDIT_CARD, LocalDateTime.now());
        Recharge recharge2 = new Recharge(2L, cardNumber, new BigDecimal("10000.00"), PaymentMethod.PSE, LocalDateTime.now());

        when(rechargeRepository.findByCardNumber(eq(cardNumber), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(recharge1, recharge2), PageRequest.of(0, 10), 2));

        Page<RechargeResponseDto> recharges = rechargeService.getRecharges(cardNumber, PageRequest.of(0, 10));

        assertEquals(2, recharges.getTotalElements());
        assertEquals(cardNumber, recharges.getContent().get(0).getCardNumber());
        assertEquals(new BigDecimal("5000.00"), recharges.getContent().get(0).getAmount());
    }

    @Test
    @DisplayName("Obtener todas las recargas cuando no se envía número de tarjeta")
    void testGetAllRecharges() {
        Recharge recharge = new Recharge(1L, "1234567890123456", new BigDecimal("25000.00"), PaymentMethod.NEQUI, LocalDateTime.now());
        when(rechargeRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(recharge), PageRequest.of(0, 10), 1));

        Page<RechargeResponseDto> recharges = rechargeService.getRecharges("   ", PageRequest.of(0, 10));

        assertEquals(1, recharges.getTotalElements());
        assertEquals("1234567890123456", recharges.getContent().get(0).getCardNumber());
        assertEquals(PaymentMethod.NEQUI, recharges.getContent().get(0).getPaymentMethod());
    }

    @Test
    @DisplayName("Crear una nueva recarga")
    void testCreateRecharge() {
        RechargeRequestDto request = new RechargeRequestDto("1234567890123456", new BigDecimal("15000.00"), PaymentMethod.DAVIPLATA);
        Recharge savedRecharge = new Recharge(7L, request.getCardNumber(), request.getAmount(), request.getPaymentMethod(), LocalDateTime.now());

        when(rechargeRepository.save(any(Recharge.class))).thenReturn(savedRecharge);

        RechargeResponseDto response = rechargeService.createRecharge(request);

        assertNotNull(response);
        assertEquals(7L, response.getId());
        assertEquals(request.getCardNumber(), response.getCardNumber());
        assertEquals(request.getAmount(), response.getAmount());
        assertEquals(request.getPaymentMethod(), response.getPaymentMethod());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    @DisplayName("Eliminar una recarga por ID")
    void testDeleteRecharge() {
        when(rechargeRepository.existsById(1L)).thenReturn(true);

        rechargeService.deleteRecharge(1L);

        verify(rechargeRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Error al eliminar una recarga inexistente")
    void testDeleteNonExistentRecharge() {
        when(rechargeRepository.existsById(99L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> rechargeService.deleteRecharge(99L));

        assertEquals("No se encuentra la recarga con ID 99", exception.getMessage());
        verify(rechargeRepository, never()).deleteById(anyLong());
    }
}
