package com.innowise.orderservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemRequest {

    @NotNull(message = "{order_item_request.order_id.null}")
    private UUID orderId;

    @NotNull(message = "{order_item_request.item_id.null}")
    private UUID itemId;

    @NotNull(message = "{order_item_request.quantity.null}")
    @Min(value = 1, message = "{order_item_request.quantity.min}")
    private Integer quantity;

}
