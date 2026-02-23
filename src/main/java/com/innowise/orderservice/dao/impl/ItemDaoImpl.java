package com.innowise.orderservice.dao.impl;

import com.innowise.orderservice.dao.ItemDao;
import com.innowise.orderservice.model.Item;
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
public class ItemDaoImpl implements ItemDao {

    private static final class SQL {
        static final String GET_ALL = "select * from items";
        static final String GET_BY_ID = "select * from items where id = ?";
        static final String GET_BY_IDS = "select * from items where id in (%s)";

        static final String CREATE =
                """
                insert into items (name, price)
                values (?, ?)
                returning *
                """;
        static final String UPDATE_BY_ID =
                """
                update items
                set name = ?,
                    price = ?
                where id = ?
                returning *
                """;

        static final String DELETE_BY_ID = "delete from items where id = ?";
    }

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Item> findAll() {
        return jdbcTemplate.query(SQL.GET_ALL, ROW_MAPPER);
    }

    @Override
    public Optional<Item> findById(UUID id) {
        try {
            Item item = jdbcTemplate.queryForObject(SQL.GET_BY_ID, ROW_MAPPER, id);
            return Optional.ofNullable(item);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format(SQL.GET_BY_IDS, placeholders);

        return jdbcTemplate.query(sql, ROW_MAPPER, ids.toArray());
    }

    @Override
    public Item create(Item item) {
        return jdbcTemplate.queryForObject(
                SQL.CREATE,
                ROW_MAPPER,
                item.getName(),
                item.getPrice()
        );
    }

    @Override
    public Item updateById(UUID id, Item item) {
        Item updatedItem = jdbcTemplate.queryForObject(
                SQL.UPDATE_BY_ID,
                ROW_MAPPER,
                item.getName(),
                item.getPrice(),
                id
        );

        if (updatedItem == null) {
            throw new EmptyResultDataAccessException("Item not found for update", 1);
        }

        return updatedItem;
    }

    @Override
    public void deleteById(UUID id) {
        jdbcTemplate.update(SQL.DELETE_BY_ID, id);
    }
}
