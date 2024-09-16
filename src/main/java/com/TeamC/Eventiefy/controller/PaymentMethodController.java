package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.entity.PaymentMethod;
import com.TeamC.Eventiefy.services.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Controller for handling payment method-related requests.
@RestController
@RequestMapping("/api/payment-methods")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;
    /**
     * Retrieves all payment methods.
     * @return a list of all payment methods
     */
    @GetMapping
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodService.getAllPaymentMethods();
    }
    /**
     * Retrieves a payment method by ID.
     * @param id the ID of the payment method
     * @return the payment method, if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethod> getPaymentMethodById(@PathVariable Long id) {
        Optional<PaymentMethod> paymentMethod = paymentMethodService.getPaymentMethodById(id);
        return paymentMethod.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    /**
     * Creates a new payment method.
     * @param paymentMethod the payment method to create
     * @return the created payment method
     */
    @PostMapping
    public ResponseEntity<?> createPaymentMethod(@Valid @RequestBody PaymentMethod paymentMethod) {
        try {
            PaymentMethod createdPaymentMethod = paymentMethodService.savePaymentMethod(paymentMethod);
            return new ResponseEntity<>(createdPaymentMethod, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    /**
     * Deletes a payment method by ID.
     * @param id the ID of the payment method to delete
     * @return a no-content response if deletion is successful, or an error message if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable Long id) {
        try {
            paymentMethodService.deletePaymentMethod(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    /**
     * Handles validation exceptions.
     * @param ex the MethodArgumentNotValidException
     * @return a map of field errors and their corresponding messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}