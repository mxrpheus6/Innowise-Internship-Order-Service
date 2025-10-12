package com.innowise.orderservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemResponse {
    private UUID id;
    private String name;
    private BigDecimal price;
}
