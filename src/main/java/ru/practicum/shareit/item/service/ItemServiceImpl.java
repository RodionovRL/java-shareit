package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.api.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutputDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.api.ItemRepository;
import ru.practicum.shareit.item.service.api.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.api.UserRepository;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
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
    public ItemOutputDto getItemById(long id, long userId) {
        Item item = findItemById(id);
        ItemOutputDto itemOutputDto;
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = now();
            itemOutputDto = findItemsWithLastAndNextBooking(item, now);
        } else {
            itemOutputDto = itemMapper.toItemOutputDto(item, null, null);
        }
        log.info("itemService: was returned item={}, by id={}", itemOutputDto, id);
        return itemOutputDto;
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto, long ownerId) {
        User owner = findUserById(ownerId);
        Item oldItem = findItemById(itemId);

        checkAccess(owner, oldItem);

        Item newItem = itemMapper.toItem(itemDto);

        newItem.setId(itemId);
        newItem.setOwner(owner);

        if (Objects.isNull(newItem.getName())) {
            newItem.setName(oldItem.getName());
        }
        if (Objects.isNull(newItem.getDescription())) {
            newItem.setDescription(oldItem.getDescription());
        }
        if (Objects.isNull(newItem.getAvailable())) {
            newItem.setAvailable(oldItem.getAvailable());
        }

        try {
            Item updatedItem = itemRepository.save(newItem);
            log.info("userService: old item={} update to new item={}", oldItem, updatedItem);
            return itemMapper.toItemDto(updatedItem);

        } catch (ConstraintViolationException e) {
            log.error("userService: NoUpdate user={} with id={} not update", newItem, itemId);
            throw new ConflictException(String.format("userService: NoUpdate user=%s with id=%s not update",
                    newItem, itemId));
        }
    }

    @Override
    public List<ItemOutputDto> getAllOwnersItems(long ownerId) {
        User owner = findUserById(ownerId);
        List<Item> items = itemRepository.findAllByOwnerOrderById(owner);
        LocalDateTime now = now();
        List<ItemOutputDto> itemOutputDtoList = findItemsWithLastAndNextBooking(items, now);
        log.info("itemService: was returned {} items ownerId={}", itemOutputDtoList.size(), ownerId);
        return itemOutputDtoList;
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text.isBlank()) {
            log.warn("itemService: text string for find is blank");
            return Collections.emptyList();
        }
        List<Item> items = itemRepository
                .findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsOrderById(text, text, true);
        log.info("itemService:  founded and returned {} items with text={} ", items.size(), text);
        return itemMapper.mapDto(items);
    }

    private List<ItemOutputDto> findItemsWithLastAndNextBooking(List<Item> items, LocalDateTime date) {
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> lastBookings = bookingRepository.findLastBookingsForItems(itemIds,
                Status.APPROVED.toString(),
                date);
        Map<Long, Booking> lastBookingMap = lastBookings.stream().collect(Collectors.toMap(
                booking -> booking.getItem().getId(), Function.identity()));

        List<Booking> nextBookings = bookingRepository.findNextBookingsForItems(itemIds,
                Status.APPROVED.toString(),
                date);
        Map<Long, Booking> nextBookingMap = nextBookings.stream().collect(Collectors.toMap(
                booking -> booking.getItem().getId(), Function.identity()));

        return items.stream()
                .map(item -> itemMapper.toItemOutputDto(item,
                        lastBookingMap.get(item.getId()),
                        nextBookingMap.get(item.getId())))
                .collect(Collectors.toList());
    }

    private ItemOutputDto findItemsWithLastAndNextBooking(Item item, LocalDateTime date) {
        return findItemsWithLastAndNextBooking(List.of(item), date).get(0);
    }

    private void checkAccess(User owner, Item itemForUpdate) {
        if (itemForUpdate.getOwner().getId().equals(owner.getId())) {
            return;
        }
        throw new NotOwnerException("only owner have access to item");
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("user with id=%s not found", userId)));
    }

    private Item findItemById(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("item with id=%s not found", itemId)));
    }
}
