package com.nishant.payment_router.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Random;

@RestController
@RequestMapping("/mock-api")
@CrossOrigin(origins = "*")
public class MockBankController {

    private static final Logger logger = LoggerFactory.getLogger(MockBankController.class);
    private final Random random = new Random();

    // 1. HDFC BANK (Primary)
    // Feature: Add "?mode=fail" to URL to force a crash for demos
    @PostMapping("/hdfc/pay")
    public ResponseEntity<String> payHDFC(@RequestParam(required = false) String mode) {
        
        // DEMO TRICK: If we send "fail", crash immediately
        if ("fail".equals(mode)) {
            logger.warn("HDFC: Manual Failure Triggered via Simulation Mode");
            return ResponseEntity.status(500).body("HDFC Down (Simulated)");
        }

        // Standard Random Failures (70% Success)
        if (random.nextInt(100) < 70) {
            logger.info("HDFC: Payment Authorized (200 OK)");
            return ResponseEntity.ok("{\"status\": \"SUCCESS\", \"bank\": \"HDFC Bank\"}");
        } else {
            logger.error("HDFC: Server Timeout (500 Error)");
            return ResponseEntity.status(500).body("HDFC Down");
        }
    }

    // 2. SBI BANK (Backup)
    @PostMapping("/sbi/pay")
    public ResponseEntity<String> paySBI(@RequestParam(required = false) String mode) {
        
        if ("fail".equals(mode)) {
             logger.warn("SBI: Manual Failure Triggered");
             return ResponseEntity.status(500).body("SBI Down (Simulated)");
        }

        // FIXED: Changed from 30 to 90 so it actually acts as a backup
        if (random.nextInt(100) < 90) {
            logger.info("SBI: Payment Authorized (200 OK)");
            return ResponseEntity.ok("{\"status\": \"SUCCESS\", \"bank\": \"State Bank of India\"}");
        } else {
            logger.error("SBI: Connection Refused (500 Error)");
            return ResponseEntity.status(500).body("SBI Down");
        }
    }
}