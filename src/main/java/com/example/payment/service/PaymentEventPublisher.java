package com.example.payment.service;

import com.example.payment.service.events.PaymentCreatedEvent;

public interface PaymentEventPublisher {
    void publishPaymentCreatedEvent(PaymentCreatedEvent event);
}