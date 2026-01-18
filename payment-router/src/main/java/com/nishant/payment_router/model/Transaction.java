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
    
    private LocalDateTime timestamp;

    // Helper constructor to make saving easier
    public Transaction(String bankName, String status, String responseMessage) {
        this.bankName = bankName;
        this.status = status;
        this.responseMessage = responseMessage;
        this.timestamp = LocalDateTime.now();
    }
    
    public Transaction() {} // Default constructor required by JPA
}