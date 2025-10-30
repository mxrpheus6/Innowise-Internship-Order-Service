package com.innowise.orderservice.kafka.consumer;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreatedEvent {
    private UUID orderId;
    private PaymentStatus paymentStatus;
}
