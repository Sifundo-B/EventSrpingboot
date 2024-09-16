package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.services.PayPalService;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/paypal")
public class PayPalController {

    @Autowired
    private PayPalService payPalService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderDetails) {
        try {
            if (!orderDetails.containsKey("amount") || !orderDetails.containsKey("eventId")) {
                return ResponseEntity.badRequest().body("Amount or eventId is missing in the order details.");
            }

            Double amount = Double.parseDouble(orderDetails.get("amount").toString());
            Long eventId = Long.parseLong(orderDetails.get("eventId").toString());
            String approvalLink = payPalService.createPayment(amount, eventId);
            return ResponseEntity.ok(Collections.singletonMap("approvalLink", approvalLink));
        } catch (PayPalRESTException e) {
            return ResponseEntity.status(500).body("Error occurred during payment creation: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unexpected error occurred during payment creation: " + e.getMessage());
        }
    }

    @GetMapping("/execute-order")
    public ResponseEntity<?> executePayment(@RequestParam String paymentId, @RequestParam String PayerID, @RequestParam Long eventId) {
        try {
            payPalService.handlePaymentSuccess(paymentId, PayerID, eventId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Payment successfully executed and ticket deducted"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Unexpected error occurred during payment execution: " + e.getMessage()));
        }
    }
}
