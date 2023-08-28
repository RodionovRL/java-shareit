package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.api.ItemRequestService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    public static final String DEFAULT_FROM = "0";
    public static final String DEFAULT_SIZE = "25";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestOutDto> postItemRequest(
            @RequestBody ItemRequestInputDto itemRequestInputDto,
            @RequestHeader(value = "X-Sharer-User-Id") long requesterId
    ) {
        log.info("receive POST request for add new itemRequest with body={}, requesterId={}",
                itemRequestInputDto, requesterId);
        ItemRequestOutDto savedItemRequestOutDto = itemRequestService.addItemRequest(itemRequestInputDto, requesterId);
        return new ResponseEntity<>(savedItemRequestOutDto, HttpStatus.CREATED);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestOutDto> getItemRequestById(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable(value = "requestId") long requestId
    ) {
        log.info("receive GET request for return itemRequest by id={}", requestId);
        ItemRequestOutDto itemRequestOutDto = itemRequestService.getItemRequestById(requestId, userId);
        return new ResponseEntity<>(itemRequestOutDto, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<ItemRequestOutDto>> getAllRequestersItemRequests(
            @RequestHeader(value = "X-Sharer-User-Id") long requesterId,
            @RequestParam(value = "from", defaultValue = DEFAULT_FROM) int from,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) int size
    ) {
        log.info("receive GET request for return all items of requesterId={} from={} size={}", requesterId, from, size);
        List<ItemRequestOutDto> itemRequestOutputDto =
                itemRequestService.getAllRequestersItemRequests(requesterId, from, size);
        return new ResponseEntity<>(itemRequestOutputDto, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestOutDto>> getAllItemRequests(
            @RequestParam(value = "from", defaultValue = DEFAULT_FROM) int from,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) int size,
            @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        log.info("receive GET request for return all items of userId={} from={} size={}", userId, from, size);
        List<ItemRequestOutDto> itemRequestOutputDto =
                itemRequestService.getAllItemRequests(userId, from, size);
        return new ResponseEntity<>(itemRequestOutputDto, HttpStatus.OK);
    }
}
