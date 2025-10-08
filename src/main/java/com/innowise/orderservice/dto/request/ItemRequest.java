package com.innowise.orderservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequest {

    @NotBlank(message = "{item_request.name.blank}")
    @Size(max = 255, message = "{item_request.name.size}")
    private String name;

    @NotNull(message = "{item_request.price.null}")
    @Digits(integer = 10, fraction = 2, message = "{item_request.price.format}")
    @DecimalMin(value = "0.01", message = "{item_request.price.min}")
    private BigDecimal price;

}
