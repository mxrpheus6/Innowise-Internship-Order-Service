package com.innowise.orderservice.dto.request;

import com.innowise.orderservice.model.enums.Status;
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
    private UUID userId;
    private Status status;
    private OffsetDateTime creationDate;

    private List<OrderItemRequest> orderItems;
}
