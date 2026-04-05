package com.example.payment.service.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCreatedEvent(
        UUID eventId,
        String payerId,
        String receiverId,
        BigDecimal amount,
        String paymentMethod
) {

    @JsonCreator
    public PaymentCreatedEvent(
            @JsonProperty("eventId") UUID eventId,
            @JsonProperty("payerId") String payerId,
            @JsonProperty("receiverId") String receiverId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("paymentMethod") String paymentMethod
    ) {
        this.eventId = eventId;
        this.payerId = payerId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }
}