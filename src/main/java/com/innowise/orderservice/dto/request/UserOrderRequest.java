package com.innowise.orderservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderRequest {

    @NotEmpty(message = "{order_request.order_items.empty}")
    private List<OrderItemRequest> orderItems;

}
