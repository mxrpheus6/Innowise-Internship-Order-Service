package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.request.ItemRequest;
import com.innowise.orderservice.dto.response.ItemResponse;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ItemService {
    ItemResponse findById(UUID id);
    List<ItemResponse> findByIds(Set<UUID> ids);
    ItemResponse create(ItemRequest itemRequest);
    ItemResponse update(UUID id, ItemRequest itemRequest);
    void deleteById(UUID id);
}
