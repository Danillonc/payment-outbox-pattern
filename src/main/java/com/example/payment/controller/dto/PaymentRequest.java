package com.example.payment.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        @NotNull UUID idempotencyKey,
        @NotNull String payerId,
        @NotNull String receiverId,
        @NotNull @Positive BigDecimal amount,
        @NotNull @Size(min = 1, max = 255) String paymentMethod
) {
}