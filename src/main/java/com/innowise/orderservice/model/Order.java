package com.innowise.orderservice.model;

import com.innowise.orderservice.model.enums.Status;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private UUID id;
    private UUID userId;
    private Status status;
    private OffsetDateTime creationDate;
}
