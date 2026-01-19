package com.nishant.payment_router.repository;

import com.nishant.payment_router.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // This gives us .save(), .findAll(), etc. for free!

    // NEW: Automatically generates a query to check for duplicates
    boolean existsByIdempotencyKey(String idempotencyKey);
}