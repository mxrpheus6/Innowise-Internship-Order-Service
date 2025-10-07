package com.innowise.orderservice.dao;

import com.innowise.orderservice.model.OrderItem;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public interface OrderItemDao {
    RowMapper<OrderItem> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> OrderItem.builder()
            .id(UUID.fromString(resultSet.getString("id")))
            .orderId(UUID.fromString(resultSet.getString("order_id")))
            .itemId(UUID.fromString(resultSet.getString("item_id")))
            .quantity(resultSet.getInt("quantity"))
            .build();

    Optional<OrderItem> findById(UUID id);
    List<OrderItem> findByOrderId(UUID orderId);
    List<OrderItem> findByItemId(UUID itemId);
    Optional<OrderItem> findByOrderAndItem(UUID orderId, UUID itemId);
    OrderItem create(OrderItem orderItem);
    OrderItem updateById(OrderItem orderItem);
    void deleteById(UUID id);
    void deleteByOrderId(UUID orderId);
    void deleteByItemId(UUID itemId);
}
