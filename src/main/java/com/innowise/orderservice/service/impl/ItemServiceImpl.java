package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.dao.ItemDao;
import com.innowise.orderservice.dto.request.ItemRequest;
import com.innowise.orderservice.dto.response.ItemResponse;
import com.innowise.orderservice.exception.custom.ItemNotFoundException;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.model.Item;
import com.innowise.orderservice.service.ItemService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemResponse> findAll() {
        return itemDao.findAll().stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    @Override
    public ItemResponse findById(UUID id) {
        Item item = itemDao.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        return itemMapper.toResponse(item);
    }

    @Override
    public List<ItemResponse> findByIds(Set<UUID> ids) {
        return itemDao.findByIds(ids).stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ItemResponse create(ItemRequest itemRequest) {
        Item item = itemMapper.toEntity(itemRequest);

        item = itemDao.create(item);

        return itemMapper.toResponse(item);
    }

    @Override
    @Transactional
    public ItemResponse update(UUID id, ItemRequest itemRequest) {
        Item item = itemMapper.toEntity(itemRequest);

        item = itemDao.updateById(id, item);

        return itemMapper.toResponse(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        itemDao.deleteById(id);
    }

}
