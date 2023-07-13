package ru.practicum.shareit.item.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemDto toItemDto(Item item);

    @Mapping(target = "owner", ignore = true)
    Item toItem(ItemDto itemDto);

    @Mapping(target = "owner", ignore = true)
    List<ItemDto> map(List<Item> items);
}
