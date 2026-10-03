package com.tullave.recargas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tullave.recargas.model.Recharge;

public interface RechargeRepository extends JpaRepository<Recharge, Long> {

    // Método para filtrar por cardNumber con paginación
    Page<Recharge> findByCardNumber(String cardNumber, Pageable pageable);
}
