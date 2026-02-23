package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.request.OrderRequest;
import com.innowise.orderservice.dto.request.UserOrderRequest;
import com.innowise.orderservice.dto.response.OrderResponse;
import com.innowise.orderservice.kafka.consumer.PaymentStatus;
import com.innowise.orderservice.model.Order;
import com.innowise.orderservice.model.enums.Status;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface OrderService {
    OrderResponse findById(UUID id);
    List<OrderResponse> findByStatus(Status status);
    List<OrderResponse> findByIds(Set<UUID> ids);
    OrderResponse create(OrderRequest orderRequest);
    OrderResponse updateById(UUID id, OrderRequest orderRequest);
    void deleteById(UUID id);
    OrderResponse createForCurrentUser(UserOrderRequest userRequest, UUID userId);
    OrderResponse updateStatusById(UUID id, PaymentStatus paymentStatus);
    List<OrderResponse> findByUserId(UUID userId);
    OrderResponse findByIdAndUserId(UUID id, UUID userId);
    OrderResponse updateByIdAndUserId(UUID id, UUID userId, OrderRequest request);
    void deleteByIdAndUserId(UUID id, UUID userId);
    List<OrderResponse> findByStatusAndUserId(Status status, UUID userId);
    List<OrderResponse> findByIdsAndUserId(Set<UUID> ids, UUID userId);
}
