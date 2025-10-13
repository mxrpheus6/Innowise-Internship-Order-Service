package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.dao.OrderItemDao;
import com.innowise.orderservice.dto.request.OrderItemRequest;
import com.innowise.orderservice.dto.response.OrderItemResponse;
import com.innowise.orderservice.exception.custom.DuplicateItemInOrderException;
import com.innowise.orderservice.exception.custom.ItemNotFoundException;
import com.innowise.orderservice.exception.custom.OrderItemNotFoundException;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.service.ItemService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.innowise.orderservice.constants.TestConstants.ITEM_ID;
import static com.innowise.orderservice.constants.TestConstants.ITEM_IDS;
import static com.innowise.orderservice.constants.TestConstants.ITEM_RESPONSE;
import static com.innowise.orderservice.constants.TestConstants.ORDER_ID;
import static com.innowise.orderservice.constants.TestConstants.ORDER_ITEM;
import static com.innowise.orderservice.constants.TestConstants.ORDER_ITEM_ID;
import static com.innowise.orderservice.constants.TestConstants.ORDER_ITEM_REQUEST;
import static com.innowise.orderservice.constants.TestConstants.ORDER_ITEM_RESPONSE;
import static com.innowise.orderservice.constants.TestConstants.QUANTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrderItemDao orderItemDao;

    @Mock
    private ItemService itemService;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;


    @Test
    void givenExistingOrderItem_whenFindById_thenReturnResponseWithItem() {
        when(orderItemDao.findById(ORDER_ITEM_ID)).thenReturn(Optional.of(ORDER_ITEM));
        when(itemService.findById(ITEM_ID)).thenReturn(ITEM_RESPONSE);
        when(orderItemMapper.toResponse(ORDER_ITEM)).thenReturn(
                OrderItemResponse.builder().orderId(ORDER_ID).quantity(QUANTITY).build()
        );

        OrderItemResponse result = orderItemService.findById(ORDER_ITEM_ID);

        assertThat(result.getItem()).isEqualTo(ITEM_RESPONSE);
        verify(orderItemDao).findById(ORDER_ITEM_ID);
        verify(itemService).findById(ITEM_ID);
        verify(orderItemMapper).toResponse(ORDER_ITEM);
    }

    @Test
    void givenNonExistingOrderItem_whenFindById_thenThrowException() {
        when(orderItemDao.findById(ORDER_ITEM_ID)).thenReturn(Optional.empty());
        assertThrows(OrderItemNotFoundException.class, () -> orderItemService.findById(ORDER_ITEM_ID));
        verify(orderItemDao).findById(ORDER_ITEM_ID);
    }

    @Test
    void givenExistingOrderItems_whenFindByOrderId_thenReturnMappedResponses() {
        when(orderItemDao.findByOrderId(ORDER_ID)).thenReturn(List.of(ORDER_ITEM));
        when(itemService.findByIds(ITEM_IDS)).thenReturn(List.of(ITEM_RESPONSE));
        when(orderItemMapper.toResponse(ORDER_ITEM))
                .thenReturn(OrderItemResponse.builder().orderId(ORDER_ID).quantity(QUANTITY).build());

        List<OrderItemResponse> result = orderItemService.findByOrderId(ORDER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getItem()).isEqualTo(ITEM_RESPONSE);

        verify(orderItemDao).findByOrderId(ORDER_ID);
        verify(itemService).findByIds(ITEM_IDS);
        verify(orderItemMapper).toResponse(ORDER_ITEM);
    }

    @Test
    void givenNoOrderItems_whenFindByOrderId_thenReturnEmptyList() {
        when(orderItemDao.findByOrderId(ORDER_ID)).thenReturn(List.of());
        List<OrderItemResponse> result = orderItemService.findByOrderId(ORDER_ID);
        assertThat(result).isEmpty();
    }

    @Test
    void givenExistingOrderItem_whenFindByOrderIdAndItemId_thenReturnResponse() {
        when(orderItemDao.findByOrderIdAndItemId(ORDER_ID, ITEM_ID)).thenReturn(Optional.of(ORDER_ITEM));
        when(orderItemMapper.toResponse(ORDER_ITEM)).thenReturn(ORDER_ITEM_RESPONSE);

        OrderItemResponse result = orderItemService.findByOrderIdAndItemId(ORDER_ID, ITEM_ID);

        assertThat(result).isEqualTo(ORDER_ITEM_RESPONSE);
        verify(orderItemDao).findByOrderIdAndItemId(ORDER_ID, ITEM_ID);
    }

    @Test
    void givenNonExistingOrderItem_whenFindByOrderIdAndItemId_thenThrowException() {
        when(orderItemDao.findByOrderIdAndItemId(ORDER_ID, ITEM_ID)).thenReturn(Optional.empty());
        assertThrows(OrderItemNotFoundException.class,
                () -> orderItemService.findByOrderIdAndItemId(ORDER_ID, ITEM_ID));
    }

    @Test
    void givenValidOrderItems_whenCreateAll_thenPersistAndReturnResponses() {
        when(itemService.findByIds(ITEM_IDS)).thenReturn(List.of(ITEM_RESPONSE));
        when(orderItemMapper.toEntity(ORDER_ITEM_REQUEST)).thenReturn(ORDER_ITEM);
        when(orderItemDao.createAll(List.of(ORDER_ITEM))).thenReturn(List.of(ORDER_ITEM));
        when(orderItemMapper.toResponse(ORDER_ITEM))
                .thenReturn(OrderItemResponse.builder().orderId(ORDER_ID).quantity(QUANTITY).build());

        List<OrderItemResponse> result = orderItemService.createAll(List.of(ORDER_ITEM_REQUEST));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getItem()).isEqualTo(ITEM_RESPONSE);
    }

    @Test
    void givenDuplicateItems_whenCreateAll_thenThrowDuplicateItemException() {
        List<OrderItemRequest> duplicates = List.of(
                ORDER_ITEM_REQUEST,
                new OrderItemRequest(ORDER_ID, ITEM_ID, 3)
        );

        assertThrows(DuplicateItemInOrderException.class, () -> orderItemService.createAll(duplicates));
    }

    @Test
    void givenMissingItems_whenCreateAll_thenThrowItemNotFoundException() {
        when(itemService.findByIds(ITEM_IDS)).thenReturn(List.of());
        assertThrows(ItemNotFoundException.class, () -> orderItemService.createAll(List.of(ORDER_ITEM_REQUEST)));
    }

    @Test
    void givenEmptyOrderItemList_whenCreateAll_thenReturnEmptyList() {
        List<OrderItemResponse> result = orderItemService.createAll(List.of());
        assertThat(result).isEmpty();
    }

    @Test
    void givenOrderItemId_whenDeleteById_thenDaoMethodInvoked() {
        doNothing().when(orderItemDao).deleteById(ORDER_ITEM_ID);
        orderItemService.deleteById(ORDER_ITEM_ID);
        verify(orderItemDao).deleteById(ORDER_ITEM_ID);
    }

    @Test
    void givenOrderId_whenDeleteByOrderId_thenDaoMethodInvoked() {
        doNothing().when(orderItemDao).deleteByOrderId(ORDER_ID);
        orderItemService.deleteByOrderId(ORDER_ID);
        verify(orderItemDao).deleteByOrderId(ORDER_ID);
    }

    @Test
    void givenItemId_whenDeleteByItemId_thenDaoMethodInvoked() {
        doNothing().when(orderItemDao).deleteByItemId(ITEM_ID);
        orderItemService.deleteByItemId(ITEM_ID);
        verify(orderItemDao).deleteByItemId(ITEM_ID);
    }
}
