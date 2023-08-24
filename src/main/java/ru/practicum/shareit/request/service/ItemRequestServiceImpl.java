package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.api.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.api.ItemRequestRepository;
import ru.practicum.shareit.request.service.api.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.api.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestOutDto addItemRequest(ItemRequestInputDto itemRequestInputDto, long requesterId) {
        User requester = findUserById(requesterId);
        ItemRequest newItemRequest = itemRequestMapper.toItemRequest(itemRequestInputDto);
        newItemRequest.setRequester(requester);
        ItemRequest addedItemRequest = itemRequestRepository.save(newItemRequest);
        log.info("itemRequestService: was add itemRequest={}", addedItemRequest);
        ItemRequestOutDto itemRequestOutDto = itemRequestMapper.toItemRequestOutDto(addedItemRequest);
        itemRequestOutDto.setItems(Collections.emptyList());
        return itemRequestOutDto;
    }

    @Override
    public ItemRequestOutDto getItemRequestById(long requestId, long userId) {
        findUserById(userId);
        ItemRequest itemRequest = findItemRequestById(requestId);
        List<Item> allRequestsItems = findAllRequestsItems(requestId);
        ItemRequestOutDto itemRequestOutDto = itemRequestMapper.toItemRequestOutDto(itemRequest);
        itemRequestOutDto.setItems(itemMapper.mapDto(allRequestsItems));
        log.info("itemRequestService: was returned itemRequest={}, for userId={}", itemRequest, userId);
        return itemRequestOutDto;
    }

    @Override
    public List<ItemRequestOutDto> getAllRequestersItemRequests(long requesterId, int from, int size) {
        User requester = findUserById(requesterId);
        int firstPage = from / size;
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequester(
                requester, PageRequest.of(firstPage, size, Sort.Direction.DESC, "created"));
        log.info("itemRequestService: was returned all {} itemRequests, for requesterId={}",
                itemRequests.size(), requesterId);
        return getRequestsWithItems(itemRequests);
    }

    @Override
    public List<ItemRequestOutDto> getAllItemRequests(long userId, int from, int size) {
        User user = findUserById(userId);
        int firstPage = from / size;
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterNot(
                user, PageRequest.of(firstPage, size, Sort.Direction.DESC, "created"));
        log.info("itemRequestService: was returned all {} itemRequests, for requesterId={}",
                itemRequests.size(), userId);
        return getRequestsWithItems(itemRequests);
    }

    private List<ItemRequestOutDto> getRequestsWithItems(List<ItemRequest> itemRequests) {
        List<Long> requestsIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> requestsItems = itemRepository.findAllByRequest_IdIn(requestsIds)
                .orElse(Collections.emptyList());
        Map<Long, List<Item>> itemsForRequests = requestsItems.stream()
                .collect(groupingBy(item -> item.getRequest().getId()));

        return itemRequests.stream()
                .map(itemRequestMapper::toItemRequestOutDto)
                .peek(itemRequestOutDto -> {
                            if (itemsForRequests.containsKey(itemRequestOutDto.getId())) {
                                itemRequestOutDto
                                        .setItems(itemMapper.mapDto(itemsForRequests.get(itemRequestOutDto.getId())));
                            }
                        }
                )
                .collect(Collectors.toList());
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("user with id=%d not found", userId)));
    }

    private ItemRequest findItemRequestById(long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("itemRequest with id=%d not found", requestId)));
    }

    private List<Item> findAllRequestsItems(long requestId) {
        return itemRepository.findAllByRequest_Id(requestId).orElse(Collections.emptyList());
    }
}
