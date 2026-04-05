package com.example.payment.service.impl;

import com.example.payment.service.PaymentEventPublisher;
import com.example.payment.service.events.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaPaymentEventPublisherImpl implements PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "payment-created";

    @Override
    public void publishPaymentCreatedEvent(PaymentCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.eventId().toString(), event);
    }
}