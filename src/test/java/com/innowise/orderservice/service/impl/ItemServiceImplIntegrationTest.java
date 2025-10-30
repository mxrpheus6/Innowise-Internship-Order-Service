package com.innowise.orderservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.innowise.orderservice.constants.TestConstants;
import com.innowise.orderservice.dao.ItemDao;
import com.innowise.orderservice.dto.request.ItemRequest;
import com.innowise.orderservice.dto.response.ItemResponse;
import com.innowise.orderservice.model.Item;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "user.service.url=none"
        }
)
public class ItemServiceImplIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.1"));

    private static final String ORDER_CREATED_TOPIC = "test-order-created";
    private static final String PAYMENT_STATUS_TOPIC = "test-payment-status";

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("app.kafka.order-created-topic", () -> ORDER_CREATED_TOPIC);
        registry.add("app.kafka.payment-status-topic", () -> PAYMENT_STATUS_TOPIC);
        registry.add("spring.kafka.consumer.group-id", () -> "test-payment-consumer-group");
        registry.add("spring.kafka.producer.key-serializer",
                () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("spring.kafka.producer.value-serializer",
                () -> "org.springframework.kafka.support.serializer.JsonSerializer");
        registry.add("spring.kafka.consumer.value-deserializer",
                () -> "org.springframework.kafka.support.serializer.JsonDeserializer");
        registry.add("spring.kafka.consumer.properties.spring.json.trusted.packages",
                () -> "*");
    }

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemDao itemDao;

    private UUID savedItemId;

    @BeforeEach
    void setUp() {
        Item item = Item.builder()
                .name(TestConstants.ITEM_NAME)
                .price(TestConstants.ITEM_PRICE)
                .build();

        Item savedItem = itemDao.create(item);
        savedItemId = savedItem.getId();
    }

    @Test
    void givenItem_whenFindById_thenReturnCorrectData() {
        ItemResponse response = itemService.findById(savedItemId);

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getId()).isEqualTo(savedItemId),
                () -> assertThat(response.getName()).isEqualTo(TestConstants.ITEM_NAME),
                () -> assertThat(response.getPrice()).isEqualTo(TestConstants.ITEM_PRICE)
        );
    }

    @Test
    void givenItemIds_whenFindByIds_thenReturnAllItems() {
        Set<UUID> ids = Set.of(savedItemId);
        List<ItemResponse> responses = itemService.findByIds(ids);
        ItemResponse response = responses.getFirst();

        assertAll(
                () -> assertThat(responses).hasSize(1),
                () -> assertThat(response.getId()).isEqualTo(savedItemId),
                () -> assertThat(response.getName()).isEqualTo(TestConstants.ITEM_NAME),
                () -> assertThat(response.getPrice()).isEqualTo(TestConstants.ITEM_PRICE)
        );
    }

    @Test
    void givenItemRequest_whenCreate_thenPersistAndReturnResponse() {
        ItemRequest request = new ItemRequest("New Item", BigDecimal.valueOf(99.99));

        ItemResponse response = itemService.create(request);

        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.getId()).isNotNull(),
                () -> assertThat(response.getName()).isEqualTo("New Item"),
                () -> assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(99.99))
        );
    }

    @Test
    void givenItemRequest_whenUpdate_thenReturnUpdatedResponse() {
        ItemRequest request = new ItemRequest("Updated Item", BigDecimal.valueOf(150.50));

        ItemResponse response = itemService.update(savedItemId, request);

        assertAll(
                () -> assertThat(response.getId()).isEqualTo(savedItemId),
                () -> assertThat(response.getName()).isEqualTo("Updated Item"),
                () -> assertThat(response.getPrice()).isEqualByComparingTo("150.50")
        );
    }

    @Test
    void givenItemId_whenDeleteById_thenRemoveFromDatabase() {
        itemService.deleteById(savedItemId);

        assertThat(itemDao.findById(savedItemId)).isEmpty();
    }
}
