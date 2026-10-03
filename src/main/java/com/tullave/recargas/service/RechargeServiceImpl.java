package com.tullave.recargas.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tullave.recargas.dto.RechargeRequestDto;
import com.tullave.recargas.dto.RechargeResponseDto;
import com.tullave.recargas.model.Recharge;
import com.tullave.recargas.repository.RechargeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RechargeServiceImpl implements RechargeService {

    private final RechargeRepository repository;

    @Override
    @Transactional
    public RechargeResponseDto createRecharge(RechargeRequestDto requestDto) {
        log.info("Creando recarga para la tarjeta #{}", requestDto.getCardNumber());

        Recharge recharge = new Recharge();
        recharge.setCardNumber(requestDto.getCardNumber());
        recharge.setAmount(requestDto.getAmount());
        recharge.setPaymentMethod(requestDto.getPaymentMethod());

        Recharge savedRecharge = repository.save(recharge);
        log.info("Recarga creada con ID {}", savedRecharge.getId());
        return mapToResponseDto(savedRecharge);
    }

    @Override
    @Transactional
    public Page<RechargeResponseDto> getRecharges(String cardNumber, Pageable pageable) {
        Page<Recharge> page;
        if (cardNumber != null && !cardNumber.trim().isEmpty()) {
            log.info("consultando recargas para la tarjeta #{}", cardNumber);
            page = repository.findByCardNumber(cardNumber.trim(), pageable);
        } else {
            log.info("consultando todas las recargas");
            page = repository.findAll(pageable);
        }

        return page.map(this::mapToResponseDto);
    }

    @Override
    @Transactional
    public void deleteRecharge(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Eliminacion fallida: No Existe la recarga con ID {}", id);
            throw new RuntimeException("No se encuentra la recarga con ID " + id);
        }

        repository.deleteById(id);
        log.info("Recarga con ID {} eliminada correctamente", id);
    }

    private RechargeResponseDto mapToResponseDto(Recharge recharge) {
        RechargeResponseDto dto = new RechargeResponseDto();
        dto.setId(recharge.getId());
        dto.setCardNumber(recharge.getCardNumber());
        dto.setAmount(recharge.getAmount());
        dto.setPaymentMethod(recharge.getPaymentMethod());
        dto.setCreatedAt(recharge.getCreatedAt());
        return dto;
    }
}
