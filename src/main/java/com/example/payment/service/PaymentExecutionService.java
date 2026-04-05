package com.example.payment.service;

import com.example.payment.service.events.PaymentCreatedEvent;

public interface PaymentExecutionService {
    void processPayment(PaymentCreatedEvent event);
}