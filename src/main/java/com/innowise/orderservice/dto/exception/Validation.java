package com.innowise.orderservice.dto.exception;

public record Validation(
        String fieldName,
        String message
) {}
