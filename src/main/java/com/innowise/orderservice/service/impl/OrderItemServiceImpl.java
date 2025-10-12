package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.dao.OrderItemDao;
import com.innowise.orderservice.dto.request.OrderItemRequest;
import com.innowise.orderservice.dto.response.ItemResponse;
import com.innowise.orderservice.dto.response.OrderItemResponse;
import com.innowise.orderservice.exception.custom.DuplicateItemInOrderException;
import com.innowise.orderservice.exception.custom.ItemNotFoundException;
import com.innowise.orderservice.exception.custom.OrderItemNotFoundException;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.model.OrderItem;
import com.innowise.orderservice.service.ItemService;
import com.innowise.orderservice.service.OrderItemService;
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
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemDao orderItemDao;
    private final ItemService itemService;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderItemResponse findById(UUID id) {
        OrderItem orderItem = orderItemDao.findById(id)
                .orElseThrow(() -> new OrderItemNotFoundException("Order-Item not found"));

        ItemResponse item = itemService.findById(orderItem.getItemId());

        OrderItemResponse response = orderItemMapper.toResponse(orderItem);
        response.setItem(item);
        return response;
    }

    @Override
    public List<OrderItemResponse> findByOrderId(UUID orderId) {
        List<OrderItem> orderItems = orderItemDao.findByOrderId(orderId);

        if (orderItems.isEmpty()) {
            return List.of();
        }

        Set<UUID> itemIds = orderItems.stream()
                .map(OrderItem::getItemId)
                .collect(Collectors.toSet());

        List<ItemResponse> items = itemService.findByIds(itemIds);

        Map<UUID, ItemResponse> itemMap = items.stream()
                .collect(Collectors.toMap(ItemResponse::getId, item -> item));

        return orderItems.stream()
                .map(orderItem -> {
                    OrderItemResponse response = orderItemMapper.toResponse(orderItem);
                    response.setItem(itemMap.get(orderItem.getItemId()));
                    return response;
                })
                .toList();
    }

    @Override
    public List<OrderItemResponse> findByOrderIds(Set<UUID> orderIds) {
        List<OrderItem> orderItems = orderItemDao.findByOrderIds(orderIds);

        if (orderItems.isEmpty()) {
            return List.of();
        }

        Set<UUID> itemIds = orderItems.stream()
                .map(OrderItem::getItemId)
                .collect(Collectors.toSet());

        List<ItemResponse> items = itemService.findByIds(itemIds);

        Map<UUID, ItemResponse> itemMap = items.stream()
                .collect(Collectors.toMap(ItemResponse::getId, item -> item));

        return orderItems.stream()
                .map(orderItem -> {
                    OrderItemResponse response = orderItemMapper.toResponse(orderItem);
                    response.setItem(itemMap.get(orderItem.getItemId()));
                    return response;
                })
                .toList();
    }

    @Override
    public List<OrderItemResponse> findByItemId(UUID itemId) {
        return orderItemDao.findByOrderId(itemId).stream()
                .map(orderItemMapper::toResponse)
                .toList();
    }

    @Override
    public OrderItemResponse findByOrderIdAndItemId(UUID orderId, UUID itemId) {
        OrderItem orderItem = orderItemDao.findByOrderIdAndItemId(orderId, itemId)
                .orElseThrow(() -> new OrderItemNotFoundException("Order-Item not found"));

        return orderItemMapper.toResponse(orderItem);
    }

    @Override
    @Transactional
    public List<OrderItemResponse> createAll(List<OrderItemRequest> orderItemRequests) {
        if (orderItemRequests == null || orderItemRequests.isEmpty()) {
            return List.of();
        }

        Set<UUID> itemIds = orderItemRequests.stream()
                .map(OrderItemRequest::getItemId)
                .collect(Collectors.toSet());

        if (orderItemRequests.size() != itemIds.size()) {
            throw new DuplicateItemInOrderException("Same items in one order");
        }

        List<ItemResponse> items = itemService.findByIds(itemIds);

        if (items.size() != itemIds.size()) {
            throw new ItemNotFoundException("Some items in the order do not exist");
        }

        List<OrderItem> orderItems = orderItemRequests.stream()
                .map(orderItemMapper::toEntity)
                .toList();

        List<OrderItem> createdOrderItems = orderItemDao.createAll(orderItems);

        Map<UUID, ItemResponse> itemMap = items.stream()
                .collect(Collectors.toMap(ItemResponse::getId, item -> item));

        return createdOrderItems.stream()
                .map(orderItem -> {
                    OrderItemResponse response = orderItemMapper.toResponse(orderItem);
                    response.setItem(itemMap.get(orderItem.getItemId()));
                    return response;
                })
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        orderItemDao.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByOrderId(UUID orderId) {
        orderItemDao.deleteByOrderId(orderId);

    }

    @Override
    @Transactional
    public void deleteByItemId(UUID itemId) {
        orderItemDao.deleteByItemId(itemId);
    }
}
