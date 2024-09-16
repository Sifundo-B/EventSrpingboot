package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Component to initialize the database with default payment methods.
@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private PaymentMethodService paymentMethodService;

    /**
     * This method will be executed when the application starts.
     * It checks if the payment methods table is empty and, if so, initializes it with some default values.
     * param args command-line arguments
     * throws Exception if an error occurs
     */
    @Override
    public void run(String... args) throws Exception {
        // Check if the payment methods table is empty
        if (paymentMethodService.getAllPaymentMethods().isEmpty()) {
            // Save default payment methods
//            paymentMethodService.savePaymentMethod(new PaymentMethod("Credit Card", "Pay using credit card"));
//            paymentMethodService.savePaymentMethod(new PaymentMethod("PayPal", "Pay using PayPal account"));
//            paymentMethodService.savePaymentMethod(new PaymentMethod("Apple Pay", "Pay using Apple Pay"));
            paymentMethodService.savePaymentMethod(new PaymentMethod("Ozow", "Pay using Ozow payment gateway"));
        }
    }
}
