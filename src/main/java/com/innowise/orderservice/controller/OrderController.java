package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.request.OrderRequest;
import com.innowise.orderservice.dto.request.UserOrderRequest;
import com.innowise.orderservice.dto.response.OrderResponse;
import com.innowise.orderservice.model.enums.Status;
import com.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getByStatus(@RequestParam Status status) {
        return ResponseEntity.ok(orderService.findByStatus(status));
    }

    @GetMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getByIds(@RequestParam Set<UUID> ids) {
        return ResponseEntity.ok(orderService.findByIds(ids));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid OrderRequest orderRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.create(orderRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateById(
            @PathVariable UUID id,
            @RequestBody @Valid OrderRequest orderRequest
    ) {
        return ResponseEntity.ok(orderService.updateById(id, orderRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<OrderResponse>> getByUserId(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) Status status) {

        UUID userId = UUID.fromString(jwt.getSubject());

        if (status != null) {
            return ResponseEntity.ok(orderService.findByStatusAndUserId(status, userId));
        }
        return ResponseEntity.ok(orderService.findByUserId(userId));
    }

    @GetMapping("/me/batch")
    public ResponseEntity<List<OrderResponse>> getByIdsAndUserId(
            @RequestParam Set<UUID> ids,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(orderService.findByIdsAndUserId(ids, userId));
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<OrderResponse> getByIdAndUserId(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(orderService.findByIdAndUserId(id, userId));
    }

    @PostMapping("/me")
    public ResponseEntity<OrderResponse> createByUserId(
            @RequestBody @Valid UserOrderRequest userOrderRequest,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createForCurrentUser(userOrderRequest, userId));
    }

    @DeleteMapping("/me/{id}")
    public ResponseEntity<Void> deleteByIdAndUserId(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());
        orderService.deleteByIdAndUserId(id, userId);
        return ResponseEntity.noContent().build();
    }
}