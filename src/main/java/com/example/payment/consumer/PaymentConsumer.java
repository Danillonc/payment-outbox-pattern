package com.example.payment.consumer;

import com.example.payment.service.PaymentExecutionService;
import com.example.payment.service.events.PaymentCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentExecutionService paymentExecutionService;

    @KafkaListener(topics = "payment-created", groupId = "payment-processor")
    public void consumePaymentEvent(String payload) {
        try {
            PaymentCreatedEvent event = objectMapper.readValue(payload, PaymentCreatedEvent.class);
            paymentExecutionService.processPayment(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize payment event", e);
        }
    }
}