package com.innowise.orderservice.service.impl;

import static com.innowise.orderservice.constants.TestConstants.ITEM;
import static com.innowise.orderservice.constants.TestConstants.ITEM_ID;
import static com.innowise.orderservice.constants.TestConstants.ITEM_IDS;
import static com.innowise.orderservice.constants.TestConstants.ITEM_REQUEST;
import static com.innowise.orderservice.constants.TestConstants.ITEM_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.innowise.orderservice.dao.ItemDao;
import com.innowise.orderservice.dto.response.ItemResponse;
import com.innowise.orderservice.exception.custom.ItemNotFoundException;
import com.innowise.orderservice.mapper.ItemMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemDao itemDao;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void testFindByIdSuccess() {
        when(itemDao.findById(ITEM_ID)).thenReturn(Optional.of(ITEM));
        when(itemMapper.toResponse(ITEM)).thenReturn(ITEM_RESPONSE);

        ItemResponse result = itemService.findById(ITEM_ID);

        assertThat(result).isEqualTo(ITEM_RESPONSE);
        verify(itemDao).findById(ITEM_ID);
        verify(itemMapper).toResponse(ITEM);
    }

    @Test
    void testFindByIdNotFound() {
        when(itemDao.findById(ITEM_ID)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findById(ITEM_ID));

        verify(itemDao).findById(ITEM_ID);
        verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void testFindByIds() {
        when(itemDao.findByIds(ITEM_IDS)).thenReturn(List.of(ITEM));
        when(itemMapper.toResponse(ITEM)).thenReturn(ITEM_RESPONSE);

        List<ItemResponse> result = itemService.findByIds(ITEM_IDS);

        assertThat(result).containsExactly(ITEM_RESPONSE);
        verify(itemDao).findByIds(ITEM_IDS);
        verify(itemMapper).toResponse(ITEM);
    }

    @Test
    void testCreate() {
        when(itemMapper.toEntity(ITEM_REQUEST)).thenReturn(ITEM);
        when(itemDao.create(ITEM)).thenReturn(ITEM);
        when(itemMapper.toResponse(ITEM)).thenReturn(ITEM_RESPONSE);

        ItemResponse result = itemService.create(ITEM_REQUEST);

        assertThat(result).isEqualTo(ITEM_RESPONSE);
        verify(itemMapper).toEntity(ITEM_REQUEST);
        verify(itemDao).create(ITEM);
        verify(itemMapper).toResponse(ITEM);
    }

    @Test
    void testUpdate() {
        when(itemMapper.toEntity(ITEM_REQUEST)).thenReturn(ITEM);
        when(itemDao.updateById(ITEM_ID, ITEM)).thenReturn(ITEM);
        when(itemMapper.toResponse(ITEM)).thenReturn(ITEM_RESPONSE);

        ItemResponse result = itemService.update(ITEM_ID, ITEM_REQUEST);

        assertThat(result).isEqualTo(ITEM_RESPONSE);
        verify(itemMapper).toEntity(ITEM_REQUEST);
        verify(itemDao).updateById(ITEM_ID, ITEM);
        verify(itemMapper).toResponse(ITEM);
    }

    @Test
    void testDeleteById() {
        doNothing().when(itemDao).deleteById(ITEM_ID);

        itemService.deleteById(ITEM_ID);

        verify(itemDao).deleteById(ITEM_ID);
    }
}

