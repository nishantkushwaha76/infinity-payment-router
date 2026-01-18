package com.nishant.payment_router.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Random;

@RestController
@RequestMapping("/mock-api")
public class MockBankController {

    private final Random random = new Random();

    // 1. HDFC BANK (Primary - 70% Success Rate)
    @PostMapping("/hdfc/pay")
    public ResponseEntity<String> payHDFC() {
        if (random.nextInt(100) < 70) {
            return ResponseEntity.ok("{\"status\": \"SUCCESS\", \"bank\": \"HDFC Bank\"}");
        } else {
            return ResponseEntity.status(500).body("HDFC Down");
        }
    }

    // 2. SBI BANK (Backup - 90% Success Rate)
    @PostMapping("/sbi/pay")
    public ResponseEntity<String> paySBI() {
        if (random.nextInt(100) < 30) {
            return ResponseEntity.ok("{\"status\": \"SUCCESS\", \"bank\": \"State Bank of India\"}");
        } else {
            return ResponseEntity.status(500).body("SBI Down");
        }
    }
}