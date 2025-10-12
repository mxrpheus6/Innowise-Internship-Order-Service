package com.innowise.orderservice.dao.impl;

import com.innowise.orderservice.dao.OrderItemDao;
import com.innowise.orderservice.model.OrderItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderItemDaoImpl implements OrderItemDao {

    private static final class SQL {
        static final String GET_BY_ID = "select * from order_items where id = ?";
        static final String GET_BY_ORDER_ID = "select * from order_items where order_id = ?";
        static final String GET_BY_ORDER_IDS = "select * from order_items WHERE order_id in (%s)";
        static final String GET_BY_ITEM_ID = "select * from order_items where item_id = ?";
        static final String GET_BY_ORDER_AND_ITEM = "select * from order_items where order_id = ? and item_id = ?";

        static final String CREATE_ALL =
            """
            insert into order_items (order_id, item_id, quantity) 
            values 
            """;

        static final String DELETE_BY_ID = "delete from order_items where id = ?";
        static final String DELETE_BY_ORDER_ID = "delete from order_items where order_id = ?";
        static final String DELETE_BY_ITEM_ID = "delete from order_items where item_id = ?";
    }

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<OrderItem> findById(UUID id) {
        try {
            OrderItem orderItem = jdbcTemplate.queryForObject(SQL.GET_BY_ID, ROW_MAPPER, id);
            return Optional.ofNullable(orderItem);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<OrderItem> findByOrderId(UUID orderId) {
        return jdbcTemplate.query(SQL.GET_BY_ORDER_ID, ROW_MAPPER, orderId);
    }

    @Override
    public List<OrderItem> findByOrderIds(Set<UUID> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return List.of();
        }

        String placeholders = String.join(",", Collections.nCopies(orderIds.size(), "?"));
        String sql = String.format(SQL.GET_BY_ORDER_IDS, placeholders);

        return jdbcTemplate.query(sql, ROW_MAPPER, orderIds.toArray());
    }

    @Override
    public List<OrderItem> findByItemId(UUID itemId) {
        return jdbcTemplate.query(SQL.GET_BY_ITEM_ID, ROW_MAPPER, itemId);
    }

    @Override
    public Optional<OrderItem> findByOrderIdAndItemId(UUID orderId, UUID itemId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SQL.GET_BY_ORDER_AND_ITEM, ROW_MAPPER, orderId, itemId));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<OrderItem> createAll(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return List.of();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(SQL.CREATE_ALL);

        List<Object> params = new ArrayList<>();
        for (int i = 0; i < orderItems.size(); i++) {
            sb.append("(?, ?, ?)");
            if (i < orderItems.size() - 1) {
                sb.append(", ");
            }
            OrderItem item = orderItems.get(i);
            params.add(item.getOrderId());
            params.add(item.getItemId());
            params.add(item.getQuantity());
        }
        sb.append(" returning id");

        List<UUID> generatedIds = jdbcTemplate.queryForList(sb.toString(), UUID.class, params.toArray());

        for (int i = 0; i < orderItems.size(); i++) {
            orderItems.get(i).setId(generatedIds.get(i));
        }

        return orderItems;
    }

    @Override
    public void deleteById(UUID id) {
        jdbcTemplate.update(SQL.DELETE_BY_ID, id);
    }

    @Override
    public void deleteByOrderId(UUID orderId) {
        jdbcTemplate.update(SQL.DELETE_BY_ORDER_ID, orderId);
    }

    @Override
    public void deleteByItemId(UUID itemId) {
        jdbcTemplate.update(SQL.DELETE_BY_ITEM_ID, itemId);
    }
}
