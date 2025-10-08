package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.request.OrderItemRequest;
import com.innowise.orderservice.dto.response.OrderItemResponse;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OrderItemService {
    OrderItemResponse findById(UUID id);
    List<OrderItemResponse> findByOrderId(UUID orderId);
    List<OrderItemResponse> findByOrderIds(Set<UUID> orderIds);
    List<OrderItemResponse> findByItemId(UUID itemId);
    OrderItemResponse findByOrderIdAndItemId(UUID orderId, UUID itemId);
    List<OrderItemResponse> createAll(List<OrderItemRequest> orderItemRequests);
    void deleteById(UUID id);
    void deleteByOrderId(UUID orderId);
    void deleteByItemId(UUID itemId);
}
