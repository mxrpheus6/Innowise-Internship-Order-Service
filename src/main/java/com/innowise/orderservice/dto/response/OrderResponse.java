package com.innowise.orderservice.dto.response;

import com.innowise.orderservice.client.user.UserResponse;
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
public class OrderResponse {
    private UUID id;
    private UserResponse user;
    private Status status;
    private OffsetDateTime creationDate;

    private List<OrderItemResponse> items;
}
