package ru.practicum.shareit.item.service.api;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItem(long itemId, ItemDto itemDto, long ownerId);

    ItemDto getItemById(long id);

    List<ItemDto> getAllOwnersItems(long ownerId);

    List<ItemDto> findItems(String text);
}
