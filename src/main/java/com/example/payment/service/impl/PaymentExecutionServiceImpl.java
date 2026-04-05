package com.example.payment.service.impl;

import com.example.payment.model.Outbox;
import com.example.payment.model.Payment;
import com.example.payment.repository.OutboxRepository;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.service.PaymentExecutionService;
import com.example.payment.service.P2PIntegrationService;
import com.example.payment.service.events.PaymentCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentExecutionServiceImpl implements PaymentExecutionService {

    private final P2PIntegrationService p2pIntegrationService;
    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void processPayment(PaymentCreatedEvent event) {
        log.info("Executing payment via P2P service for event: {}", event.eventId());
        boolean success = p2pIntegrationService.executePayment(event);

        if (success) {
            log.info("P2P payment successful. Persisting transaction and creating outbox event for: {}", event.eventId());

            // 1. Create and save the primary business entity (the payment)
            Payment payment = new Payment(
                    event.payerId(),
                    event.receiverId(),
                    event.amount(),
                    event.paymentMethod()
            );
            Payment savedPayment = paymentRepository.save(payment);

            // 2. Create and save the outbox event within the same transaction
            try {
                String payload = objectMapper.writeValueAsString(savedPayment);
                Outbox outboxEvent = new Outbox(
                        "payment_processed", // The type of event for consumers
                        savedPayment.getId(),      // The aggregate ID
                        payload                    // The event payload
                );
                outboxRepository.save(outboxEvent);
                log.info("Outbox event created successfully for payment: {}", savedPayment.getId());
            } catch (JsonProcessingException e) {
                log.error("FATAL: Could not serialize payment data for outbox event. Transaction will be rolled back.", e);
                // This is a critical internal error. The transaction will be rolled back,
                // but this indicates a problem with our domain model or ObjectMapper.
                throw new RuntimeException("Failed to serialize outbox event payload", e);
            }
        } else {
            log.warn("P2P payment failed for event: {}. This will trigger the DLQ flow.", event.eventId());
            // This exception will be caught by the consumer's error handler,
            // triggering retries and eventually the DLQ.
            throw new RuntimeException("Payment failed in P2P integration for event: " + event.eventId());
        }
    }
}