package com.example.payment.consumer;

import com.example.payment.service.PaymentExecutionService;
import com.example.payment.service.events.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentExecutionService paymentExecutionService;

    @KafkaListener(topics = "payment-created", groupId = "payment-processor")
    public void consumePaymentEvent(PaymentCreatedEvent event) {
        try {
            log.info("Processing payment event: {}", event.eventId());
            paymentExecutionService.processPayment(event);
            log.info("Payment event processed successfully: {}", event.eventId());
        } catch (Exception e) {
            log.error("Error processing payment event: {}. Handing over to DLQ handler.", event.eventId(), e);
            // By re-throwing the exception, we delegate the error handling
            // to the configured DefaultErrorHandler, which will manage retries
            // and eventually send the message to the Dead Letter Queue.
            throw e;
        }
    }
}