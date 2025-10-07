package com.innowise.orderservice.dao.impl;

import com.innowise.orderservice.dao.OrderItemDao;
import com.innowise.orderservice.model.OrderItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderItemDaoImpl implements OrderItemDao {

    private static final class SQL {
        static final String GET_BY_ID = "select * from order_items where id = ?";
        static final String GET_BY_ORDER_ID = "select * from order_items where order_id = ?";
        static final String GET_BY_ITEM_ID = "select * from order_items where item_id = ?";
        static final String GET_BY_ORDER_AND_ITEM = "select * from order_items where order_id = ? and item_id = ?";


        static final String CREATE =
                """
                insert into order_items (order_id, item_id, quantity)
                values (?, ?, ?)
                returning id
                """;
        static final String UPDATE_BY_ID =
                """
                update order_items
                set order_id = ?,
                    item_id = ?,
                    quantity = ?
                where id = ?
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
    public List<OrderItem> findByItemId(UUID itemId) {
        return jdbcTemplate.query(SQL.GET_BY_ITEM_ID, ROW_MAPPER, itemId);
    }

    @Override
    public Optional<OrderItem> findByOrderAndItem(UUID orderId, UUID itemId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SQL.GET_BY_ORDER_AND_ITEM, ROW_MAPPER, orderId, itemId));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public OrderItem create(OrderItem orderItem) {
        UUID id = jdbcTemplate.queryForObject(
                SQL.CREATE,
                UUID.class,
                orderItem.getOrderId(),
                orderItem.getItemId(),
                orderItem.getQuantity()
        );
        orderItem.setId(id);
        return orderItem;
    }

    @Override
    public OrderItem updateById(OrderItem orderItem) {
        int updatedRows = jdbcTemplate.update(
                SQL.UPDATE_BY_ID,
                orderItem.getOrderId(),
                orderItem.getItemId(),
                orderItem.getQuantity(),
                orderItem.getId()
        );

        if (updatedRows == 0) {
            throw new EmptyResultDataAccessException("Order-Item with id " + orderItem.getId() + " not found for update", 1);
        }

        return orderItem;
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
