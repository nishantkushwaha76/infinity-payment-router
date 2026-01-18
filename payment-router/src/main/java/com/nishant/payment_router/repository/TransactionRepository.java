package com.nishant.payment_router.repository;

import com.nishant.payment_router.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // This gives us .save(), .findAll(), .findById() for free!
}