package com.example.payment.service.impl;

import com.example.payment.service.P2PIntegrationService;
import com.example.payment.service.events.PaymentCreatedEvent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FakeP2PIntegrationServiceImpl implements P2PIntegrationService {

    @Override
    public boolean executePayment(PaymentCreatedEvent event) {
        // Simulate failure for amounts that are multiples of 10
        if (event.amount().remainder(BigDecimal.TEN).compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        return true;
    }
}