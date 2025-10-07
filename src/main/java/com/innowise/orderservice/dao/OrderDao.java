package com.innowise.orderservice.dao;

import com.innowise.orderservice.model.Order;
import com.innowise.orderservice.model.enums.Status;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public interface OrderDao {
    RowMapper<Order> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Order.builder()
            .id(UUID.fromString(resultSet.getString("id")))
            .userId(UUID.fromString(resultSet.getString("user_id")))
            .status(Status.valueOf(resultSet.getString("status")))
            .creationDate(resultSet.getObject("creation_date", OffsetDateTime.class))
            .build();

    Optional<Order> findById(UUID id);
    List<Order> findByStatus(Status status);
    List<Order> findByIds(Set<UUID> ids);
    Order create(Order order);
    Order updateById(Order order);
    void deleteById(UUID id);
}
