package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.request.ItemRequest;
import com.innowise.orderservice.dto.response.ItemResponse;
import com.innowise.orderservice.service.ItemService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(itemService.findById(id));
    }

    @GetMapping("/batch")
    public ResponseEntity<List<ItemResponse>> getByIds(@RequestParam Set<UUID> ids) {
        return ResponseEntity.ok(itemService.findByIds(ids));
    }

    @PostMapping
    public ResponseEntity<ItemResponse> create(@RequestBody ItemRequest itemRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.create(itemRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> updateById(
            @PathVariable UUID id,
            @RequestBody ItemRequest itemRequest
    ) {
        return ResponseEntity.ok(itemService.update(id, itemRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        itemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
