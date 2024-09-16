package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.PaymentMethod;
import com.TeamC.Eventiefy.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    /**
     * Retrieves all payment methods.
     * @return a list of all payment methods
     */
    @Override
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    /**
     * Retrieves a payment method by its ID.
     * @param id the ID of the payment method
     * @return the payment method, if found
     */
    @Override
    public Optional<PaymentMethod> getPaymentMethodById(Long id) {
        return paymentMethodRepository.findById(id);
    }

    /**
     * Saves a new payment method.
     * @param paymentMethod the payment method to save
     * @return the saved payment method
     * @throws IllegalArgumentException if a payment method with the same name already exists
     */
    @Override
    public PaymentMethod savePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethodRepository.existsByName(paymentMethod.getName())) {
            throw new IllegalArgumentException("Payment method with the same name already exists");
        }
        return paymentMethodRepository.save(paymentMethod);
    }

    /**
     * Deletes a payment method by its ID.
     * @param id the ID of the payment method to delete
     * @throws IllegalArgumentException if the payment method is not found
     */
    @Override
    public void deletePaymentMethod(Long id) {
        if (!paymentMethodRepository.existsById(id)) {
            throw new IllegalArgumentException("Payment method not found");
        }
        paymentMethodRepository.deleteById(id);
    }
}