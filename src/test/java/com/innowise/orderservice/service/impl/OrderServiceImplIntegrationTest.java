package com.innowise.orderservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.orderservice.client.user.UserResponse;
import com.innowise.orderservice.constants.TestConstants;
import com.innowise.orderservice.dao.OrderDao;
import com.innowise.orderservice.dto.request.OrderRequest;
import com.innowise.orderservice.dto.response.OrderResponse;
import com.innowise.orderservice.model.Order;
import com.innowise.orderservice.model.enums.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "user.service.url=http://localhost:${wiremock.server.port}"
        }
)
@EnableWireMock
class OrderServiceImplIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Value("${wiremock.server.baseUrl}")
    private String wireMockUrl;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        userId = TestConstants.USER_ID;

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(Status.NEW);

        Order saved = orderDao.create(order);
        orderId = saved.getId();
    }

    @Test
    @Transactional
    void givenOrdersWithStatus_whenFindByStatus_thenReturnOrdersWithUsersAndItems() throws Exception {
        UserResponse user = TestConstants.USER_RESPONSE;

        stubFor(get(urlPathEqualTo(TestConstants.GET_USERS_BY_IDS_URL))
                .withQueryParam("ids", matching(".*"))
                .willReturn(okJson(objectMapper.writeValueAsString(List.of(user)))));

        List<OrderResponse> result = orderService.findByStatus(Status.NEW);

        assertNotNull(result);
        assertEquals(1, result.size());

        OrderResponse response = result.getFirst();

        assertEquals(orderId, response.getId());
        assertEquals(userId, response.getUser().getId());
        assertEquals(TestConstants.USER_NAME, response.getUser().getName());
        assertEquals(Status.NEW, response.getStatus());
    }

    @Test
    @Transactional
    void givenOrderId_whenFindById_thenReturnOrderWithUser() throws Exception {
        UserResponse user = TestConstants.USER_RESPONSE;

        stubFor(get(urlPathEqualTo(TestConstants.GET_USER_BY_ID_URL + userId))
                .willReturn(okJson(objectMapper.writeValueAsString(user))));

        OrderResponse response = orderService.findById(orderId);

        assertNotNull(response);
        assertEquals(orderId, response.getId());
        assertEquals(userId, response.getUser().getId());
        assertEquals(TestConstants.USER_NAME, response.getUser().getName());
    }

    @Test
    @Transactional
    void givenValidOrderRequest_whenCreate_thenPersistOrderAndReturnResponse() throws Exception {
        UUID newUserId = TestConstants.USER_ID_2;
        UserResponse user = TestConstants.USER_RESPONSE_2;

        stubFor(get(urlPathEqualTo(TestConstants.GET_USER_BY_ID_URL + newUserId))
                .willReturn(okJson(objectMapper.writeValueAsString(user))));

        OrderRequest request = new OrderRequest();
        request.setUserId(newUserId);
        request.setStatus(Status.NEW);

        OrderResponse response = orderService.create(request);

        assertNotNull(response.getId());
        assertEquals(newUserId, response.getUser().getId());
        assertEquals(Status.NEW, response.getStatus());

        Order fromDb = orderDao.findById(response.getId()).orElseThrow();
        assertEquals(newUserId, fromDb.getUserId());
    }

    @Test
    @Transactional
    void givenExistingOrderAndUpdateRequest_whenUpdateById_thenUpdateOrderSuccessfully() throws Exception {
        UserResponse user = TestConstants.USER_RESPONSE_3;

        stubFor(get(urlPathEqualTo(TestConstants.GET_USER_BY_ID_URL + TestConstants.USER_ID_3))
                .willReturn(okJson(objectMapper.writeValueAsString(user))));

        OrderRequest updateRequest = new OrderRequest();
        updateRequest.setUserId(TestConstants.USER_ID_3);
        updateRequest.setStatus(Status.SHIPPED);

        OrderResponse updated = orderService.updateById(orderId, updateRequest);

        assertNotNull(updated);
        assertEquals(orderId, updated.getId());
        assertEquals(Status.SHIPPED, updated.getStatus());
        assertEquals(TestConstants.USER_NAME_3, updated.getUser().getName());

        Order fromDb = orderDao.findById(orderId).orElseThrow();
        assertEquals(Status.SHIPPED, fromDb.getStatus());
    }

    @Test
    @Transactional
    void givenOrderId_whenDeleteById_thenRemoveOrderFromDb() {
        orderService.deleteById(orderId);

        assertTrue(orderDao.findById(orderId).isEmpty());
    }
}