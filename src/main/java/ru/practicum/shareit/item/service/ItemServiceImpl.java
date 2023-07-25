package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.api.ItemRepository;
import ru.practicum.shareit.item.service.api.ItemService;
import ru.practicum.shareit.item.repository.api.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.api.UserRepository;
import ru.practicum.shareit.user.repository.api.UserStorage;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    private final ItemMapper itemMapper;

    public ItemDto addItem(ItemDto newItemDto, long userId) {
        User owner = findUserById(userId);
        Item newItem = itemMapper.toItem(newItemDto);

        newItem.setOwner(owner);

        Item addedItem = itemRepository.save(newItem);
        log.info("itemService: was add item={}", addedItem);

        return itemMapper.toItemDto(addedItem);
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto, long ownerId) {
        User owner = userStorage.getUserById(ownerId);
        Item oldItem = itemStorage.getItemById(itemId);

        checkAccess(owner, oldItem);

        Item newItem = itemMapper.toItem(itemDto);
        Item updatedItem = itemStorage.updateItem(itemId, newItem);
        log.info("itemService: item={} was updated to item={}", oldItem, updatedItem);

        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(long id) {
        Item item = itemStorage.getItemById(id);
        log.info("itemService: was returned item={}, by id={}", item, id);
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllOwnersItems(long ownerId) {
        User owner = userStorage.getUserById(ownerId);
        List<Item> items = itemStorage.getAllOwnersItems(owner);
        log.info("itemService: was returned {} items ownerId={}", items.size(), ownerId);
        return itemMapper.map(items);
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text.isBlank()) {
            log.warn("itemService: text string for find is blank");
            return Collections.emptyList();
        }
        List<Item> items = itemStorage.findItems(text);
        log.info("itemService:  founded and returned {} items with text={} ", items.size(), text);
        return itemMapper.map(items);
    }

    private void checkAccess(User owner, Item itemForUpdate) {
        if (itemForUpdate.getOwner().getId() == owner.getId()) {
            return;
        }
        throw new NotOwnerException("only owner have access to item");
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("user with id=%s not found", userId)));
    }
}
