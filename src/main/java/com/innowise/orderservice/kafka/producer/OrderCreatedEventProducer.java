package com.innowise.orderservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreatedEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Value("${kafka.topics.create-order}")
    private String createOrderTopic;

    public void send(OrderCreatedEvent event) {
        kafkaTemplate.send(createOrderTopic, event);
    }
}
