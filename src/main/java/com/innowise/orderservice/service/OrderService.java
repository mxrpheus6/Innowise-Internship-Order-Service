package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.request.OrderRequest;
import com.innowise.orderservice.dto.response.OrderResponse;
import com.innowise.orderservice.kafka.consumer.PaymentStatus;
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
    OrderResponse updateStatusById(UUID id, PaymentStatus paymentStatus);
}
