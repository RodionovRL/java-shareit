package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.api.ItemStorage;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        checkItemIsExist(id);
        return items.get(id);
    }

    @Override
    public List<Item> getAllOwnersItems(User owner) {
        return items.values().stream()
                .filter(i -> i.getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItems(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    private long getNewId() {
        long newId = ++ids;
        log.trace("create new itemId={}", newId);
        return newId;
    }

    private void checkItemIsExist(Long id) {
        if (!items.containsKey(id)) {
            log.error("item with id={} not found", id);
            throw new NotFoundException(String.format(
                    "item with id=%s not found", id));
        }
    }
}
