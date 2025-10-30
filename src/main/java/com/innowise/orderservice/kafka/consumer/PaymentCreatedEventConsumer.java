package com.innowise.orderservice.kafka.consumer;

import com.innowise.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCreatedEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "CREATE_PAYMENT")
    public void consume(PaymentCreatedEvent event) {
        orderService.updateStatusById(event.getOrderId(), event.getPaymentStatus());
    }

}
