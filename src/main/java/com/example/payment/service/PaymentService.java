package com.example.payment.service;

import com.example.payment.controller.dto.PaymentRequest;

public interface PaymentService {
    void processPayment(PaymentRequest paymentRequest);
}