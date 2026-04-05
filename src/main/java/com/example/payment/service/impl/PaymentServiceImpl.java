package com.example.payment.service.impl;

import com.example.payment.controller.dto.PaymentRequest;
import com.example.payment.model.IdempotencyKey;
import com.example.payment.repository.IdempotencyKeyRepository;
import com.example.payment.service.PaymentEventPublisher;
import com.example.payment.service.PaymentService;
import com.example.payment.service.events.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    @Override
    @Transactional
    public void processPayment(PaymentRequest paymentRequest) {
        if (idempotencyKeyRepository.existsById(paymentRequest.idempotencyKey())) {
            return;
        }

        idempotencyKeyRepository.save(new IdempotencyKey(paymentRequest.idempotencyKey()));

        PaymentCreatedEvent event = new PaymentCreatedEvent(
                paymentRequest.idempotencyKey(),
                paymentRequest.payerId(),
                paymentRequest.receiverId(),
                paymentRequest.amount(),
                paymentRequest.paymentMethod()
        );

        paymentEventPublisher.publishPaymentCreatedEvent(event);
    }
}