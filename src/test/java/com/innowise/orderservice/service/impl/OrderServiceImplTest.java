package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.user.UserFeignClient;
import com.innowise.orderservice.dao.OrderDao;
import com.innowise.orderservice.dto.request.OrderRequest;
import com.innowise.orderservice.dto.response.OrderResponse;
import com.innowise.orderservice.exception.custom.OrderNotFoundException;
import com.innowise.orderservice.kafka.producer.OrderCreatedEventProducer;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.Order;
import com.innowise.orderservice.model.enums.Status;
import com.innowise.orderservice.service.OrderItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.innowise.orderservice.constants.TestConstants.ORDER_ID;
import static com.innowise.orderservice.constants.TestConstants.ORDER_ITEM_REQUEST;
import static com.innowise.orderservice.constants.TestConstants.ORDER_ITEM_RESPONSE;
import static com.innowise.orderservice.constants.TestConstants.USER_ID;
import static com.innowise.orderservice.constants.TestConstants.USER_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private OrderCreatedEventProducer orderCreatedEventProducer;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void givenExistingOrder_whenFindById_thenReturnOrderResponseWithItemsAndUser() {
        Order order = new Order(ORDER_ID, USER_ID, Status.NEW, OffsetDateTime.now());
        OrderResponse response = new OrderResponse(ORDER_ID, null, Status.NEW, order.getCreationDate(), List.of());

        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderItemService.findByOrderId(ORDER_ID)).thenReturn(List.of(ORDER_ITEM_RESPONSE));
        when(userFeignClient.getUserById(order.getUserId())).thenReturn(USER_RESPONSE);
        when(orderMapper.toResponse(order)).thenReturn(response);

        OrderResponse result = orderService.findById(ORDER_ID);

        assertThat(result.getId()).isEqualTo(ORDER_ID);
        assertThat(result.getItems()).containsExactly(ORDER_ITEM_RESPONSE);
        assertThat(result.getUser()).isEqualTo(USER_RESPONSE);
        verify(orderDao).findById(ORDER_ID);
    }

    @Test
    void givenNonExistingOrder_whenFindById_thenThrowOrderNotFoundException() {
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.findById(ORDER_ID));
        verify(orderDao).findById(ORDER_ID);
    }

    @Test
    void givenOrdersWithStatus_whenFindByStatus_thenReturnOrderResponsesWithItemsAndUsers() {
        Order order = new Order(ORDER_ID, USER_ID, Status.NEW, OffsetDateTime.now());
        OrderResponse response = new OrderResponse(ORDER_ID, null, Status.NEW, order.getCreationDate(), List.of());

        when(orderDao.findByStatus(Status.NEW)).thenReturn(List.of(order));
        when(orderItemService.findByOrderIds(Set.of(ORDER_ID))).thenReturn(List.of(ORDER_ITEM_RESPONSE));
        when(userFeignClient.getUsersByIds(List.of(order.getUserId()))).thenReturn(List.of(USER_RESPONSE));
        when(orderMapper.toResponse(order)).thenReturn(response);

        List<OrderResponse> result = orderService.findByStatus(Status.NEW);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getItems()).containsExactly(ORDER_ITEM_RESPONSE);
        assertThat(result.getFirst().getUser()).isEqualTo(USER_RESPONSE);
    }

    @Test
    void givenValidOrderRequest_whenCreate_thenPersistOrderAndReturnResponseWithItemsAndUser() {
        Order orderEntity = new Order(null, USER_ID, Status.NEW, OffsetDateTime.now());
        Order savedOrder = new Order(ORDER_ID, USER_ID, Status.NEW, OffsetDateTime.now());
        OrderRequest orderRequest = new OrderRequest(USER_ID, Status.NEW, List.of(ORDER_ITEM_REQUEST));
        OrderResponse response = new OrderResponse(ORDER_ID, null, Status.NEW, savedOrder.getCreationDate(), List.of());

        when(userFeignClient.getUserById(orderRequest.getUserId())).thenReturn(USER_RESPONSE);
        when(orderMapper.toEntity(orderRequest)).thenReturn(orderEntity);
        when(orderDao.create(orderEntity)).thenReturn(savedOrder);
        when(orderItemService.createAll(orderRequest.getOrderItems())).thenReturn(List.of(ORDER_ITEM_RESPONSE));
        when(orderMapper.toResponse(savedOrder)).thenReturn(response);

        OrderResponse result = orderService.create(orderRequest);

        assertThat(result.getId()).isEqualTo(ORDER_ID);
        assertThat(result.getItems()).containsExactly(ORDER_ITEM_RESPONSE);
        assertThat(result.getUser()).isEqualTo(USER_RESPONSE);
    }

    @Test
    void givenExistingOrderAndValidUpdateRequest_whenUpdateById_thenUpdateOrderAndReturnResponseWithItemsAndUser() {
        Order existingOrder = new Order(ORDER_ID, USER_ID, Status.NEW, OffsetDateTime.now());
        Order updatedOrder = new Order(ORDER_ID, USER_ID, Status.NEW, OffsetDateTime.now());
        OrderRequest orderRequest = new OrderRequest(USER_ID, Status.NEW, List.of(ORDER_ITEM_REQUEST));
        OrderResponse response = new OrderResponse(ORDER_ID, null, Status.NEW, updatedOrder.getCreationDate(), List.of(ORDER_ITEM_RESPONSE));

        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.of(existingOrder));
        when(userFeignClient.getUserById(orderRequest.getUserId())).thenReturn(USER_RESPONSE);
        when(orderDao.updateById(ORDER_ID, existingOrder)).thenReturn(updatedOrder);
        doNothing().when(orderItemService).deleteByOrderId(ORDER_ID);
        when(orderItemService.createAll(orderRequest.getOrderItems())).thenReturn(List.of(ORDER_ITEM_RESPONSE));
        when(orderMapper.toResponse(updatedOrder)).thenReturn(response);

        OrderResponse result = orderService.updateById(ORDER_ID, orderRequest);

        assertThat(result.getId()).isEqualTo(ORDER_ID);
        assertThat(result.getItems()).containsExactly(ORDER_ITEM_RESPONSE);
        assertThat(result.getUser()).isEqualTo(USER_RESPONSE);

        verify(orderItemService).deleteByOrderId(ORDER_ID);
        verify(orderItemService).createAll(orderRequest.getOrderItems());
    }

    @Test
    void givenOrderId_whenDeleteById_thenInvokeDaoDelete() {
        doNothing().when(orderDao).deleteById(ORDER_ID);
        orderService.deleteById(ORDER_ID);
        verify(orderDao).deleteById(ORDER_ID);
    }
}
