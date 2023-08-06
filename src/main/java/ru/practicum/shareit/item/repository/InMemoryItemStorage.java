package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.api.ItemStorage;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage {
    private static long ids;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item newItem) {
        long id = getNewId();
        newItem.setId(id);
        items.put(id, newItem);
        return newItem;
    }

    @Override
    public Item updateItem(long id, Item newItem) {
        Item oldItem = getItemById(id);
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        items.replace(id, oldItem);
        return oldItem;
    }

    @Override
    public Item getItemById(Long id) {
        Item item = items.get(id);
        if (item == null) {
            log.error("item with id={} not found", id);
            throw new NotFoundException(String.format(
                    "item with id=%s not found", id));
        }
        return item;
    }

    @Override
    public List<Item> getAllOwnersItems(User owner) {
        return items.values().stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), owner.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItems(String text) {
        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(i -> i.getName().toLowerCase().contains(lowerCaseText) ||
                        i.getDescription().toLowerCase().contains(lowerCaseText))
                .collect(Collectors.toList());
    }

    private static long getNewId() {
        long newId = ++ids;
        log.trace("created new itemId={}", newId);
        return newId;
    }
}
