package com.nishant.payment_router.controller;

import com.nishant.payment_router.model.Transaction;
import com.nishant.payment_router.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RouterController {

    private static final Logger logger = LoggerFactory.getLogger(RouterController.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final TransactionRepository transactionRepository;

    public RouterController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // 1. PAYMENT LOGIC (Now Protected by Idempotency)
    @PostMapping("/make-payment")
    public ResponseEntity<String> processPayment(@RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        
        // --- 🛡️ IDEMPOTENCY CHECK (Safety Layer) ---
        // If the frontend didn't send a key, we generate a temporary one (for testing)
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            idempotencyKey = "TEMP_" + System.currentTimeMillis(); 
        }

        // Check if this key already exists in the database
        if (transactionRepository.existsByIdempotencyKey(idempotencyKey)) {
            logger.warn("⚠️ Duplicate Transaction Blocked! Key: " + idempotencyKey);
            return ResponseEntity.status(409).body("{\"status\": \"DUPLICATE\", \"message\": \"Transaction already processed.\"}");
        }
        // -------------------------------------------------

        // A. TRY HDFC FIRST
        try {
            logger.info("Router: Processing Transaction " + idempotencyKey + ". Attempting HDFC...");
            String hdfcUrl = "http://localhost:8080/mock-api/hdfc/pay";
            String response = restTemplate.postForObject(hdfcUrl, null, String.class);
            
            // Save success to DB (WITH KEY)
            transactionRepository.save(new Transaction("HDFC Bank", "SUCCESS", "Primary Route", idempotencyKey));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.warn("HDFC Failed. Switching to SBI...");
            
            // B. TRY SBI BACKUP
            try {
                String sbiUrl = "http://localhost:8080/mock-api/sbi/pay";
                String response = restTemplate.postForObject(sbiUrl, null, String.class);

                // Save success to DB (WITH KEY)
                transactionRepository.save(new Transaction("SBI", "SUCCESS", "Backup Route", idempotencyKey));
                return ResponseEntity.ok(response);

            } catch (Exception ex) {
                // C. FALLBACK TRIGGER
                logger.error("CRITICAL: Both Banks Failed.");
                
                // Save failure to DB (WITH KEY)
                transactionRepository.save(new Transaction("System Alert", "FALLBACK_TRIGGERED", "Redirecting to Gateway", idempotencyKey));
                return ResponseEntity.ok("{\"status\": \"FALLBACK\", \"message\": \"USE_RAZORPAY\"}");
            }
        }
    }

    // 2. GATEWAY SUCCESS ENDPOINT (For the OTP Modal)
    @PostMapping("/gateway/simulate-success")
    public ResponseEntity<String> gatewaySuccess() {
        logger.info("Gateway: Payment recovered successfully via Secure OTP.");
        
        // We generate a new unique key for the recovery transaction
        String recoveryKey = "REC_" + System.currentTimeMillis();
        
        transactionRepository.save(new Transaction("Razorpay Gateway", "SUCCESS", "Recovered via Fallback", recoveryKey));
        return ResponseEntity.ok("Saved");
    }

    // 3. DASHBOARD ENDPOINT (For admin.html)
    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        logger.info("Admin Dashboard: Fetching live transaction logs...");
        return transactionRepository.findAll();
    }
}