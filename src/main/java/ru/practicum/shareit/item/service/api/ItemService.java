package ru.practicum.shareit.item.service.api;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItem(long itemId, ItemDto itemDto, long ownerId);

    ItemOutputDto getItemById(long id, long userId);

    List<ItemOutputDto> getAllOwnersItems(long ownerId);

    List<ItemDto> findItems(String text);
}
