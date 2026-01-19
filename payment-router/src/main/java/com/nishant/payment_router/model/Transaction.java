package com.nishant.payment_router.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankName; // e.g., "HDFC" or "SBI"
    private String status;   // "SUCCESS" or "FAILED"
    private String responseMessage;
    
    // NEW FIELD: Stores the unique key to prevent double-spending
    @Column(unique = true) // Optional: Ensures database rejects duplicates
    private String idempotencyKey;

    private LocalDateTime timestamp;

    // Updated Helper Constructor (Now includes idempotencyKey)
    public Transaction(String bankName, String status, String responseMessage, String idempotencyKey) {
        this.bankName = bankName;
        this.status = status;
        this.responseMessage = responseMessage;
        this.idempotencyKey = idempotencyKey;
        this.timestamp = LocalDateTime.now();
    }
    
    // Default constructor required by JPA
    public Transaction() {} 
}