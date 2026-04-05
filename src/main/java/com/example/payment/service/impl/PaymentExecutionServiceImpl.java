package com.example.payment.service.impl;

import com.example.payment.model.Payment;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.service.PaymentExecutionService;
import com.example.payment.service.P2PIntegrationService;
import com.example.payment.service.events.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentExecutionServiceImpl implements PaymentExecutionService {

    private final P2PIntegrationService p2pIntegrationService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void processPayment(PaymentCreatedEvent event) {
        boolean success = p2pIntegrationService.executePayment(event);

        if (success) {
            Payment payment = new Payment(
                    event.payerId(),
                    event.receiverId(),
                    event.amount(),
                    event.paymentMethod()
            );
            paymentRepository.save(payment);
        } else {
            // This exception will trigger the DLQ mechanism
            throw new RuntimeException("Payment failed in P2P integration");
        }
    }
}