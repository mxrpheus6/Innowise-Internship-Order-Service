package com.innowise.orderservice.dao;

import com.innowise.orderservice.model.Item;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public interface ItemDao {
    RowMapper<Item> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Item.builder()
            .id(UUID.fromString(resultSet.getString("id")))
            .name(resultSet.getString("name"))
            .price(resultSet.getBigDecimal("price"))
            .build();

    Optional<Item> findById(UUID id);
    List<Item> findByIds(Set<UUID> ids);
    Item create(Item item);
    Item updateById(Item item);
    void deleteById(UUID id);
}
