package com.innowise.orderservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreatedEventProducer {
    private static final String TOPIC = "CREATE_ORDER";
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void send(OrderCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}
