package com.innowise.orderservice.dao.impl;

import com.innowise.orderservice.dao.OrderDao;
import com.innowise.orderservice.model.Order;
import com.innowise.orderservice.model.enums.Status;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderDaoImpl implements OrderDao {

    private static final class SQL {
        static final String GET_BY_ID = "select * from orders where id = ?";
        static final String GET_BY_STATUS = "select * from orders where status = ?";
        static final String GET_BY_IDS = "select * from orders where id in (%s)";

        static final String CREATE =
                """
                insert into orders (user_id, status, creation_date)
                values (?, ?, CURRENT_TIMESTAMP)
                returning *
                """;
        static final String UPDATE_BY_ID =
                """
                update orders
                set user_id = ?,
                    status = ?
                where id = ?
                returning *
                """;

        static final String DELETE_BY_ID = "delete from orders where id = ?";
    }

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Order> findById(UUID id) {
        try {
            Order order = jdbcTemplate.queryForObject(SQL.GET_BY_ID, ROW_MAPPER, id);
            return Optional.ofNullable(order);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Order> findByStatus(Status status) {
        return jdbcTemplate.query(SQL.GET_BY_STATUS, ROW_MAPPER, status.name());
    }

    @Override
    public List<Order> findByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format(SQL.GET_BY_IDS, placeholders);

        return jdbcTemplate.query(sql, ROW_MAPPER, ids.toArray());
    }

    @Override
    public Order create(Order order) {
        return jdbcTemplate.queryForObject(
                SQL.CREATE,
                ROW_MAPPER,
                order.getUserId(),
                order.getStatus().name()
        );
    }

    @Override
    public Order updateById(UUID id, Order order) {
        Order updatedOrder = jdbcTemplate.queryForObject(
                SQL.UPDATE_BY_ID,
                ROW_MAPPER,
                order.getUserId(),
                order.getStatus().name(),
                id
        );

        if (updatedOrder == null) {
            throw new EmptyResultDataAccessException("Order not found for update", 1);
        }

        return updatedOrder;
    }

    @Override
    public void deleteById(UUID id) {
        jdbcTemplate.update(SQL.DELETE_BY_ID, id);
    }

}
