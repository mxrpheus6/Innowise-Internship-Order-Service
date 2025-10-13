package com.innowise.orderservice.constants;

import com.innowise.orderservice.client.user.UserResponse;
import com.innowise.orderservice.dto.request.ItemRequest;
import com.innowise.orderservice.dto.request.OrderItemRequest;
import com.innowise.orderservice.dto.response.ItemResponse;
import com.innowise.orderservice.dto.response.OrderItemResponse;
import com.innowise.orderservice.model.Item;
import com.innowise.orderservice.model.OrderItem;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestConstants {

    public static final UUID ITEM_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    public static final String ITEM_NAME = "Test Item";
    public static final BigDecimal ITEM_PRICE = BigDecimal.valueOf(99.99);

    public static final Item ITEM = new Item(ITEM_ID, ITEM_NAME, ITEM_PRICE);
    public static final ItemRequest ITEM_REQUEST = new ItemRequest(ITEM_NAME, ITEM_PRICE);
    public static final ItemResponse ITEM_RESPONSE = new ItemResponse(ITEM_ID, ITEM_NAME, ITEM_PRICE);
    public static final Set<UUID> ITEM_IDS = Set.of(ITEM_ID);

    public static final UUID ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID ORDER_ITEM_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final Integer QUANTITY = 5;

    public static final OrderItem ORDER_ITEM = new OrderItem(ORDER_ITEM_ID, ORDER_ID, ITEM_ID, QUANTITY);
    public static final OrderItemRequest ORDER_ITEM_REQUEST = new OrderItemRequest(ORDER_ID, ITEM_ID, QUANTITY);
    public static final OrderItemResponse ORDER_ITEM_RESPONSE = OrderItemResponse.builder()
            .orderId(ORDER_ID)
            .item(ITEM_RESPONSE)
            .quantity(QUANTITY)
            .build();
    public static final OrderItem SAVED_ORDER_ITEM = new OrderItem(
            ORDER_ITEM_ID,
            ORDER_ID,
            ITEM_ID,
            QUANTITY
    );

    public static final UUID USER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final String USER_NAME = "John";
    public static final String USER_SURNAME = "Doe";
    public static final LocalDate USER_BIRTH_DATE = LocalDate.of(1990, 1, 1);
    public static final String USER_EMAIL = "john.doe@example.com";

    public static final String GET_USER_BY_ID_URL = "/api/v1/users/";
    public static final String GET_USERS_BY_IDS_URL = "/api/v1/users/batch";

    public static final UserResponse USER_RESPONSE = new UserResponse(
            USER_ID,
            USER_NAME,
            USER_SURNAME,
            USER_BIRTH_DATE,
            USER_EMAIL
    );

    public static final UUID USER_ID_2 = UUID.fromString("44444444-4444-4444-4444-444444444444");
    public static final String USER_NAME_2 = "Bob";
    public static final String USER_SURNAME_2 = "Johnson";
    public static final LocalDate USER_BIRTH_DATE_2 = LocalDate.of(1992, 3, 10);
    public static final String USER_EMAIL_2 = "bob.johnson@example.com";
    public static final UserResponse USER_RESPONSE_2 = new UserResponse(
            USER_ID_2,
            USER_NAME_2,
            USER_SURNAME_2,
            USER_BIRTH_DATE_2,
            USER_EMAIL_2
    );

    public static final UUID USER_ID_3 = UUID.fromString("55555555-5555-5555-5555-555555555555");
    public static final String USER_NAME_3 = "Charlie";
    public static final String USER_SURNAME_3 = "Williams";
    public static final LocalDate USER_BIRTH_DATE_3 = LocalDate.of(1995, 2, 28);
    public static final String USER_EMAIL_3 = "charlie.williams@example.com";
    public static final UserResponse USER_RESPONSE_3 = new UserResponse(
            USER_ID_3,
            USER_NAME_3,
            USER_SURNAME_3,
            USER_BIRTH_DATE_3,
            USER_EMAIL_3
    );

}
