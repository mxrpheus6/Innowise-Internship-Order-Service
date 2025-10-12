package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.request.OrderItemRequest;
import com.innowise.orderservice.dto.response.OrderItemResponse;
import com.innowise.orderservice.model.OrderItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(
        componentModel = ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderItemMapper {
    OrderItemResponse toResponse(OrderItem orderItem);
    OrderItem toEntity(OrderItemRequest orderItemRequest);
}
