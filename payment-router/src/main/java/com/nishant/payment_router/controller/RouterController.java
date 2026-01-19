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

    // 1. Initialize the Logger (Industry Standard)
    private static final Logger logger = LoggerFactory.getLogger(RouterController.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final TransactionRepository transactionRepository;

    public RouterController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // 2. THE MAIN PAYMENT LOGIC (Hybrid Routing)
    @PostMapping("/make-payment")
    public ResponseEntity<String> processPayment() {
        
        // A. TRY HDFC FIRST (Primary Route)
        try {
            logger.info("Router: Initiating transaction. Attempting HDFC Primary Route...");
            String hdfcUrl = "http://localhost:8080/mock-api/hdfc/pay";
            String response = restTemplate.postForObject(hdfcUrl, null, String.class);
            
            // Save success to DB
            transactionRepository.save(new Transaction("HDFC Bank", "SUCCESS", "Primary Route"));
            logger.info("Transaction Successful via HDFC Bank.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.warn("HDFC Connection Failed (500 Error). Switching to SBI Backup Route...");
            
            // B. TRY SBI BACKUP (Secondary Route)
            try {
                String sbiUrl = "http://localhost:8080/mock-api/sbi/pay";
                String response = restTemplate.postForObject(sbiUrl, null, String.class);

                // Save success to DB
                transactionRepository.save(new Transaction("SBI", "SUCCESS", "Backup Route"));
                logger.info("Transaction Recovered via SBI Backup.");
                return ResponseEntity.ok(response);

            } catch (Exception ex) {
                // C. BOTH FAILED -> ACTIVATE STEALTH GATEWAY (Frontend Trigger)
                logger.error("CRITICAL: Both Banks Failed. Triggering Hybrid Gateway Fallback.");
                
                // Log the failure in DB (Status: FALLBACK_TRIGGERED)
                transactionRepository.save(new Transaction("System Alert", "FALLBACK_TRIGGERED", "Redirecting to Gateway"));
                
                // Send signal to Frontend to open the OTP Modal
                return ResponseEntity.ok("{\"status\": \"FALLBACK\", \"message\": \"USE_RAZORPAY\"}");
            }
        }
    }

    // 3. GATEWAY SUCCESS ENDPOINT (For the OTP Modal)
    @PostMapping("/gateway/simulate-success")
    public ResponseEntity<String> gatewaySuccess() {
        logger.info("Gateway: Payment recovered successfully via Secure OTP.");
        transactionRepository.save(new Transaction("Razorpay Gateway", "SUCCESS", "Recovered via Fallback"));
        return ResponseEntity.ok("Saved");
    }

    // 4. DASHBOARD ENDPOINT (For admin.html)
    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        logger.info("Admin Dashboard: Fetching live transaction logs...");
        return transactionRepository.findAll();
    }
}