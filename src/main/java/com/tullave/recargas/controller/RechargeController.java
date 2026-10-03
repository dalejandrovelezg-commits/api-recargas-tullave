package com.tullave.recargas.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tullave.recargas.dto.RechargeRequestDto;
import com.tullave.recargas.dto.RechargeResponseDto;
import com.tullave.recargas.service.RechargeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RechargeController {

    private final RechargeService rechargeService;

    @PostMapping("/recharges")
    public ResponseEntity<RechargeResponseDto> createRecharge(@Valid @RequestBody RechargeRequestDto requestDto) {
        RechargeResponseDto responseDto = rechargeService.createRecharge(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/getRecharges")
    public ResponseEntity<Page<RechargeResponseDto>> getRecharges(
            @RequestParam(required = false) String cardNumberString,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RechargeResponseDto> recharges = rechargeService.getRecharges(cardNumberString, pageable);
        return new ResponseEntity<>(recharges, HttpStatus.OK);
    }

    @DeleteMapping("/recharges/{id}")
    public ResponseEntity<Void> deleteRecharge(@PathVariable Long id) {
        rechargeService.deleteRecharge(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
