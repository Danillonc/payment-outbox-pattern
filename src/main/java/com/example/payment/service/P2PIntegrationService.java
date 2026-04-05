package com.example.payment.service;

import com.example.payment.service.events.PaymentCreatedEvent;

public interface P2PIntegrationService {
    boolean executePayment(PaymentCreatedEvent event);
}