package com.innowise.orderservice.exception.advice;

import com.innowise.orderservice.dto.exception.ExceptionDto;
import com.innowise.orderservice.dto.exception.Validation;
import com.innowise.orderservice.exception.custom.DuplicateItemInOrderException;
import com.innowise.orderservice.exception.custom.ItemNotFoundException;
import com.innowise.orderservice.exception.custom.OrderItemNotFoundException;
import com.innowise.orderservice.exception.custom.OrderNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String FIELD_VALIDATION_FAILED = "Field validation failed";
    private static final String PARAMETER_TYPE_MISMATCH = "Parameter '%s' must be a '%s'";

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        String message = String.format(
                PARAMETER_TYPE_MISMATCH,
                e.getName(),
                e.getRequiredType()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDto(LocalDateTime.now(), message, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        List<Validation> validations = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new Validation(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionDto(LocalDateTime.now(), FIELD_VALIDATION_FAILED, validations));
    }

    @ExceptionHandler(DuplicateItemInOrderException.class)
    public ResponseEntity<ExceptionDto> handleDuplicateItemInOrderException(DuplicateItemInOrderException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ExceptionDto(LocalDateTime.now(), e.getMessage(), null));
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleItemNotFoundException(ItemNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDto(LocalDateTime.now(), e.getMessage(), null));
    }

    @ExceptionHandler(OrderItemNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleOrderItemNotFoundException(OrderItemNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDto(LocalDateTime.now(), e.getMessage(), null));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleOrderNotFoundException(OrderNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDto(LocalDateTime.now(), e.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionDto(LocalDateTime.now(), e.getMessage(), null));
    }

}
