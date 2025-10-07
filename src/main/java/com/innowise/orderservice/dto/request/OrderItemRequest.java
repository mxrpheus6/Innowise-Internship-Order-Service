package com.innowise.orderservice.dto.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemRequest {
    private UUID orderId;
    private UUID itemId;
    private Integer quantity;
}
