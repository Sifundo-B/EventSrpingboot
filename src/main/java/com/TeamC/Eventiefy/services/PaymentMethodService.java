package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.PaymentMethod;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodService {
     //Get all payment methods.
     List<PaymentMethod> getAllPaymentMethods();
    //Get a payment method by ID.
    Optional<PaymentMethod> getPaymentMethodById(Long id);
    //Save a payment method
    PaymentMethod savePaymentMethod(PaymentMethod paymentMethod);
    // Delete a payment method by ID.
    void deletePaymentMethod(Long id);
}
