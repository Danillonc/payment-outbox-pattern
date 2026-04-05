package com.example.payment.service.events;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCreatedEvent(
        UUID eventId,
        String payerId,
        String receiverId,
        BigDecimal amount,
        String paymentMethod
) {
}