package com.nishant.payment_router.controller;

import com.nishant.payment_router.model.Transaction;
import com.nishant.payment_router.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RouterController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TransactionRepository transactionRepository;

    public RouterController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // 1. THE MAIN PAYMENT LOGIC (Hybrid Routing)
    @PostMapping("/make-payment")
    public ResponseEntity<String> processPayment() {
        
        // A. TRY HDFC FIRST (Primary Route)
        try {
            System.out.println("Router: Attempting HDFC...");
            String hdfcUrl = "http://localhost:8080/mock-api/hdfc/pay";
            String response = restTemplate.postForObject(hdfcUrl, null, String.class);
            
            // Save success to DB
            transactionRepository.save(new Transaction("HDFC Bank", "SUCCESS", "Primary Route"));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("HDFC Failed. Switching to SBI...");
            
            // B. TRY SBI BACKUP (Secondary Route)
            try {
                String sbiUrl = "http://localhost:8080/mock-api/sbi/pay";
                String response = restTemplate.postForObject(sbiUrl, null, String.class);

                // Save success to DB
                transactionRepository.save(new Transaction("SBI", "SUCCESS", "Backup Route"));
                return ResponseEntity.ok(response);

            } catch (Exception ex) {
                // C. BOTH FAILED -> ACTIVATE STEALTH GATEWAY (Frontend Trigger)
                System.out.println("Critical Failure. Activating Replica Gateway.");
                
                // Log the failure in DB (Status: FALLBACK_TRIGGERED)
                transactionRepository.save(new Transaction("System Alert", "FALLBACK_TRIGGERED", "Redirecting to Gateway"));
                
                // Send signal to Frontend to open the OTP Modal
                return ResponseEntity.ok("{\"status\": \"FALLBACK\", \"message\": \"USE_RAZORPAY\"}");
            }
        }
    }

    // 2. GATEWAY SUCCESS ENDPOINT (For the OTP Modal)
    @PostMapping("/gateway/simulate-success")
    public ResponseEntity<String> gatewaySuccess() {
        System.out.println("Gateway: Payment Recovered via Secure OTP");
        transactionRepository.save(new Transaction("Razorpay Gateway", "SUCCESS", "Recovered via Fallback"));
        return ResponseEntity.ok("Saved");
    }

    // 3. DASHBOARD ENDPOINT (For admin.html)
    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        // Added this log so you can see when the Admin page connects!
        System.out.println("Admin Dashboard requested transaction data.");
        return transactionRepository.findAll();
    }
}