package com.innowise.orderservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    @JsonIgnore
    private UUID orderId;
    private ItemResponse item;
    private Integer quantity;
}
