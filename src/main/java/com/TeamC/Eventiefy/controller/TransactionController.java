package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.entity.Transaction;
import com.TeamC.Eventiefy.services.TransactionService;
import com.TeamC.Eventiefy.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/process")
    public ResponseEntity<?> processTransaction(@RequestBody Map<String, Object> transactionDetails, Principal principal) {
        try {
            String payerName = (String) transactionDetails.get("payerName");
            String payerEmail = (String) transactionDetails.get("payerEmail");
            String transactionId = (String) transactionDetails.get("transactionId");
            String paymentMethod = (String) transactionDetails.get("paymentMethod");
            Double amount = ((Number) transactionDetails.get("amount")).doubleValue();
            LocalDateTime transactionDate = LocalDateTime.parse((String) transactionDetails.get("transactionDate"));

            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Transaction transaction = new Transaction();
            transaction.setPayerName(payerName);
            transaction.setPayerEmail(payerEmail);
            transaction.setTransactionId(transactionId);
            transaction.setPaymentMethod(paymentMethod);
            transaction.setAmount(amount);
            transaction.setTransactionDate(transactionDate);
            transaction.setUser(currentUser);

            transactionService.save(transaction);

            return ResponseEntity.ok("Transaction processed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Transaction processing failed: " + e.getMessage());
        }
    }
}
