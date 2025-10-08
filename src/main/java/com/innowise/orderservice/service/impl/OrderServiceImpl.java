package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.user.UserFeignClient;
import com.innowise.orderservice.client.user.UserResponse;
import com.innowise.orderservice.dao.OrderDao;
import com.innowise.orderservice.dto.request.OrderRequest;
import com.innowise.orderservice.dto.response.OrderItemResponse;
import com.innowise.orderservice.dto.response.OrderResponse;
import com.innowise.orderservice.exception.custom.OrderNotFoundException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.Order;
import com.innowise.orderservice.model.enums.Status;
import com.innowise.orderservice.service.OrderItemService;
import com.innowise.orderservice.service.OrderService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;
    private final OrderItemService orderItemService;
    private final OrderMapper orderMapper;

    private final UserFeignClient userFeignClient;

    @Override
    public OrderResponse findById(UUID id) {
        Order order = orderDao.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        List<OrderItemResponse> items = orderItemService.findByOrderId(id);
        UserResponse user = userFeignClient.getUserById(order.getUserId());

        OrderResponse response = orderMapper.toResponse(order);
        response.setItems(items);
        response.setUser(user);
        return response;
    }

    @Override
    public List<OrderResponse> findByStatus(Status status) {
        List<Order> orders = orderDao.findByStatus(status);
        if (orders.isEmpty()) {
            return List.of();
        }

        List<UUID> orderIds = orders.stream()
                .map(Order::getId)
                .toList();

        List<UUID> userIds = orders.stream()
                .map(Order::getUserId)
                .toList();

        List<OrderItemResponse> allItems = orderItemService.findByOrderIds(Set.copyOf(orderIds));

        List<UserResponse> users = userFeignClient.getUsersByIds(userIds);
        Map<UUID, UserResponse> usersById = users.stream()
                .collect(Collectors.toMap(UserResponse::getId, u -> u));


        Map<UUID, List<OrderItemResponse>> itemsByOrderId = allItems.stream()
                .collect(Collectors.groupingBy(OrderItemResponse::getOrderId));

        return orders.stream()
                .map(order -> {
                    OrderResponse response = orderMapper.toResponse(order);
                    response.setItems(itemsByOrderId.getOrDefault(order.getId(), List.of()));
                    response.setUser(usersById.get(order.getUserId()));
                    return response;
                })
                .toList();
    }

    @Override
    public List<OrderResponse> findByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<Order> orders = orderDao.findByIds(ids);
        if (orders.isEmpty()) {
            return List.of();
        }

        List<UUID> orderIds = orders.stream()
                .map(Order::getId)
                .toList();

        List<UUID> userIds = orders.stream()
                .map(Order::getUserId)
                .toList();

        List<OrderItemResponse> allItems = orderItemService.findByOrderIds(Set.copyOf(orderIds));

        List<UserResponse> users = userFeignClient.getUsersByIds(userIds);
        Map<UUID, UserResponse> usersById = users.stream()
                .collect(Collectors.toMap(UserResponse::getId, u -> u));


        Map<UUID, List<OrderItemResponse>> itemsByOrderId = allItems.stream()
                .collect(Collectors.groupingBy(OrderItemResponse::getOrderId));

        return orders.stream()
                .map(order -> {
                    OrderResponse response = orderMapper.toResponse(order);
                    response.setItems(itemsByOrderId.getOrDefault(order.getId(), List.of()));
                    response.setUser(usersById.get(order.getUserId()));
                    return response;
                })
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse create(OrderRequest orderRequest) {
        UserResponse user = userFeignClient.getUserById(orderRequest.getUserId());

        Order order = orderMapper.toEntity(orderRequest);

        Order savedOrder = orderDao.create(order);

        List<OrderItemResponse> savedItems = List.of();
        if (orderRequest.getOrderItems() != null && !orderRequest.getOrderItems().isEmpty()) {
            orderRequest.getOrderItems().forEach(item -> item.setOrderId(savedOrder.getId()));

            savedItems = orderItemService.createAll(orderRequest.getOrderItems());
        }

        OrderResponse response = orderMapper.toResponse(savedOrder);
        response.setItems(savedItems);
        response.setUser(user);

        return response;
    }

    @Override
    @Transactional
    public OrderResponse updateById(UUID id, OrderRequest orderRequest) {
        UserResponse user = userFeignClient.getUserById(orderRequest.getUserId());


        Order existingOrder = orderDao.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        existingOrder.setUserId(orderRequest.getUserId());
        existingOrder.setStatus(orderRequest.getStatus());
        existingOrder.setCreationDate(orderRequest.getCreationDate());

        Order updatedOrder = orderDao.updateById(id, existingOrder);

        List<OrderItemResponse> updatedItems = List.of();

        if (orderRequest.getOrderItems() != null && !orderRequest.getOrderItems().isEmpty()) {
            orderItemService.deleteByOrderId(id);

            orderRequest.getOrderItems().forEach(item -> item.setOrderId(id));
            updatedItems = orderItemService.createAll(orderRequest.getOrderItems());
        }

        OrderResponse response = orderMapper.toResponse(updatedOrder);
        response.setItems(updatedItems);
        response.setUser(user);

        return response;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        orderDao.deleteById(id);
    }
}
