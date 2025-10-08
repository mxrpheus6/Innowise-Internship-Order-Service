package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.request.ItemRequest;
import com.innowise.orderservice.dto.response.ItemResponse;
import com.innowise.orderservice.model.Item;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ItemMapper {
    ItemResponse toResponse(Item item);
    Item toEntity(ItemRequest itemRequest);
}
