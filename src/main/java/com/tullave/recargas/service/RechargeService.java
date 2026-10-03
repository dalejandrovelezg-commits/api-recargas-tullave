package com.tullave.recargas.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tullave.recargas.dto.RechargeRequestDto;
import com.tullave.recargas.dto.RechargeResponseDto;

public interface RechargeService {

    RechargeResponseDto createRecharge(RechargeRequestDto requestDto);

    Page<RechargeResponseDto> getRecharges(String cardNumber, Pageable pageable);

    void deleteRecharge(Long id);
}
