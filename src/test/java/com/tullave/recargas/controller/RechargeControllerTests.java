package com.tullave.recargas.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tullave.recargas.dto.RechargeRequestDto;
import com.tullave.recargas.dto.RechargeResponseDto;
import com.tullave.recargas.exception.ApiExceptionHandler;
import com.tullave.recargas.exception.NotFoundException;
import com.tullave.recargas.model.PaymentMethod;
import com.tullave.recargas.service.RechargeService;

@ExtendWith(MockitoExtension.class)
class RechargeControllerTests {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RechargeService rechargeService;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new RechargeController(rechargeService))
                .setControllerAdvice(new ApiExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/recharges crea una recarga correctamente")
    void createRecharge_returnsCreated() throws Exception {
        RechargeRequestDto requestDto = new RechargeRequestDto(
                "1234567890123456",
                new BigDecimal("5000.00"),
                PaymentMethod.CREDIT_CARD
        );

        RechargeResponseDto responseDto = new RechargeResponseDto(
                1L,
                "1234567890123456",
                new BigDecimal("5000.00"),
                PaymentMethod.CREDIT_CARD,
                LocalDateTime.of(2025, 1, 15, 10, 30, 0)
        );

        when(rechargeService.createRecharge(any(RechargeRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/recharges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cardNumber").value("1234567890123456"))
                .andExpect(jsonPath("$.amount").value(5000.00))
                .andExpect(jsonPath("$.paymentMethod").value("CREDIT_CARD"));
    }

    @Test
    @DisplayName("POST /api/v1/recharges responde 400 cuando la solicitud es inválida")
    void createRecharge_invalidRequest_returnsBadRequest() throws Exception {
        RechargeRequestDto invalidRequest = new RechargeRequestDto(
                "123",
                new BigDecimal("1000.00"),
                PaymentMethod.CREDIT_CARD
        );

        mockMvc.perform(post("/api/v1/recharges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(rechargeService, never()).createRecharge(any(RechargeRequestDto.class));
    }

    @Test
    @DisplayName("GET /api/v1/getRecharges devuelve la página de recargas")
    void getRecharges_returnsPage() throws Exception {
        RechargeResponseDto responseDto = new RechargeResponseDto(
                2L,
                "1234567890123456",
                new BigDecimal("15000.00"),
                PaymentMethod.PSE,
                LocalDateTime.of(2025, 2, 10, 8, 0, 0)
        );

        when(rechargeService.getRecharges(eq("1234567890123456"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(responseDto), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/getRecharges")
                        .param("cardNumberString", "1234567890123456")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].cardNumber").value("1234567890123456"))
                .andExpect(jsonPath("$.content[0].amount").value(15000.00))
                .andExpect(jsonPath("$.content[0].paymentMethod").value("PSE"));
    }

    @Test
    @DisplayName("DELETE /api/v1/recharges/{id} elimina la recarga correctamente")
    void deleteRecharge_returnsNoContent() throws Exception {
        doNothing().when(rechargeService).deleteRecharge(7L);

        mockMvc.perform(delete("/api/v1/recharges/{id}", 7L))
                .andExpect(status().isNoContent());

        verify(rechargeService).deleteRecharge(7L);
    }

    @Test
    @DisplayName("DELETE /api/v1/recharges/{id} responde 404 si la recarga no existe")
    void deleteRecharge_nonExistentId_returnsNotFound() throws Exception {
        doThrow(new NotFoundException("No se encuentra la recarga con ID 999"))
                .when(rechargeService).deleteRecharge(999L);

        mockMvc.perform(delete("/api/v1/recharges/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(rechargeService).deleteRecharge(999L);
    }
}
