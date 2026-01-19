package com.nishant.payment_router;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentRouterApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentRouterApplication.class, args);
        
        // This prints a clickable link in your VS Code Terminal
        System.out.println("\n\n" +
            "=========================================================\n" +
            "🚀  Infinity Payment Router is Live! \n" +
            "---------------------------------------------------------\n" +
            "💳  Payment Page:   http://localhost:8080/index.html \n" +
            "📊  Admin Panel:    http://localhost:8080/admin.html \n" +
            "=========================================================\n");
    }

}