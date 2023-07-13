package ru.practicum.shareit.item.storage.api;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item newItem);

    Item updateItem(long id, Item item);

    Item getItemById(Long id);

    List<Item> getAllOwnersItems(User owner);

    List<Item> findItems(String text);
}
