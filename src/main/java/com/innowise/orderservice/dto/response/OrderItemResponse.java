package com.innowise.orderservice.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemResponse {
    private UUID id;
    private UUID orderId;
    private UUID itemId;
    private Integer quantity;
}
