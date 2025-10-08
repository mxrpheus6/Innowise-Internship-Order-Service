package com.innowise.orderservice.dto.request;

import com.innowise.orderservice.model.enums.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "{order_request.user_id.null}")
    private UUID userId;

    @NotNull(message = "{order_request.user_id.null}")
    private Status status;

    @NotEmpty(message = "{order_request.order_items.empty}")
    private List<OrderItemRequest> orderItems;

}
