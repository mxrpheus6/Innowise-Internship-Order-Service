package com.innowise.orderservice.dto.response;

import com.innowise.orderservice.model.enums.Status;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private UUID userId;
    private Status status;
    private OffsetDateTime creationDate;
}
