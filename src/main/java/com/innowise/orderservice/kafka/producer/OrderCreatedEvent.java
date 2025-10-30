package com.innowise.orderservice.kafka.producer;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderCreatedEvent {
    private UUID orderId;
    private UUID userId;
    private BigDecimal total;
}
