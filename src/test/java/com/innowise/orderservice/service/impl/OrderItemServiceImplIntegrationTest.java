package com.innowise.orderservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.innowise.orderservice.constants.TestConstants;
import com.innowise.orderservice.dao.ItemDao;
import com.innowise.orderservice.dao.OrderDao;
import com.innowise.orderservice.dao.OrderItemDao;
import com.innowise.orderservice.dto.request.OrderItemRequest;
import com.innowise.orderservice.dto.response.OrderItemResponse;
import com.innowise.orderservice.model.Item;
import com.innowise.orderservice.model.Order;
import com.innowise.orderservice.model.OrderItem;
import com.innowise.orderservice.model.enums.Status;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "user.service.url=none"
        }
)
@Transactional
public class OrderItemServiceImplIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private OrderItemServiceImpl orderItemService;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ItemDao itemDao;

    private UUID savedOrderId;
    private UUID savedOrderItemId;
    private UUID savedItemId;

    @BeforeEach
    void setUp() {
        Order order = Order.builder()
                .userId(UUID.randomUUID())
                .status(Status.NEW)
                .creationDate(OffsetDateTime.now())
                .build();
        Order savedOrder = orderDao.create(order);
        savedOrderId = savedOrder.getId();

        Item item = Item.builder()
                .name(TestConstants.ITEM_NAME)
                .price(TestConstants.ITEM_PRICE)
                .build();
        Item savedItem = itemDao.create(item);

        savedItemId = savedItem.getId();

        OrderItem orderItem = OrderItem.builder()
                .orderId(savedOrderId)
                .itemId(savedItemId)
                .quantity(TestConstants.QUANTITY)
                .build();

        orderItemDao.createAll(List.of(orderItem));
        savedOrderItemId = orderItem.getId();
    }

    @Test
    void givenOrderItem_whenFindById_thenReturnCorrectData() {
        OrderItemResponse response = orderItemService.findById(savedOrderItemId);

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getItem().getId()).isEqualTo(savedItemId),
                () -> assertThat(response.getItem().getName()).isEqualTo(TestConstants.ITEM_NAME),
                () -> assertThat(response.getItem().getPrice()).isEqualTo(TestConstants.ITEM_PRICE),
                () -> assertThat(response.getQuantity()).isEqualTo(TestConstants.QUANTITY)
        );
    }

    @Test
    void givenOrderId_whenFindByOrderId_thenReturnAllOrderItems() {
        List<OrderItemResponse> responses = orderItemService.findByOrderId(savedOrderId);
        OrderItemResponse response = responses.getFirst();

        assertAll(
                () -> assertThat(responses).hasSize(1),
                () -> assertThat(response.getItem().getId()).isEqualTo(savedItemId),
                () -> assertThat(response.getItem().getName()).isEqualTo(TestConstants.ITEM_NAME),
                () -> assertThat(response.getItem().getPrice()).isEqualTo(TestConstants.ITEM_PRICE),
                () -> assertThat(response.getQuantity()).isEqualTo(TestConstants.QUANTITY)
        );
    }

    @Test
    void givenOrderItemRequest_whenCreateAll_thenPersistAndReturnResponses() {
        Item newItem = new Item();
        newItem.setName("Another item");
        newItem.setPrice(BigDecimal.valueOf(50));
        Item savedNewItem = itemDao.create(newItem);

        OrderItemRequest request = new OrderItemRequest(savedOrderId, savedNewItem.getId(), 10);

        List<OrderItemResponse> responses = orderItemService.createAll(List.of(request));
        OrderItemResponse response = responses.getFirst();

        assertAll(
                () -> assertThat(responses).hasSize(1),
                () -> assertThat(response.getItem().getId()).isEqualTo(savedNewItem.getId()),
                () -> assertThat(response.getItem().getName()).isEqualTo(savedNewItem.getName()),
                () -> assertThat(response.getItem().getPrice()).isEqualTo(savedNewItem.getPrice()),
                () -> assertThat(response.getQuantity()).isEqualTo(10)
        );
    }

    @Test
    void givenOrderItem_whenDeleteById_thenRemoveFromDatabase() {
        orderItemService.deleteById(savedOrderItemId);

        assertThat(orderItemDao.findById(savedOrderItemId)).isEmpty();
    }

    @Test
    void givenOrderId_whenDeleteByOrderId_thenRemoveAllItemsForOrder() {
        orderItemService.deleteByOrderId(savedOrderId);

        assertThat(orderItemDao.findByOrderId(savedOrderId)).isEmpty();
    }

    @Test
    void givenItemId_whenDeleteByItemId_thenRemoveAllMatchingItems() {
        orderItemService.deleteByItemId(savedItemId);

        assertThat(orderItemDao.findByItemId(savedItemId)).isEmpty();
    }
}
