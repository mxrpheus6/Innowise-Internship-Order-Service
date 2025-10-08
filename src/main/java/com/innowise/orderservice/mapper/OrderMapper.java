package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.request.OrderRequest;
import com.innowise.orderservice.dto.response.OrderResponse;
import com.innowise.orderservice.model.Order;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderMapper {
    OrderResponse toResponse(Order order);
    Order toEntity(OrderRequest orderRequest);
}
