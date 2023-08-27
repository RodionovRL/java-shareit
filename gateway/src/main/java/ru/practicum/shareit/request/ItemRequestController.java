package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    public static final String DEFAULT_FROM = "0";
    public static final String DEFAULT_SIZE = "25";

    @PostMapping
    public ResponseEntity<Object> postItemRequest(
            @RequestHeader(value = "X-Sharer-User-Id") long requesterId,
            @Valid @RequestBody ItemRequestInputDto itemRequestInputDto
    ) {
        log.info("GW receive POST request for add new itemRequest with body={}, requesterId={}",
                itemRequestInputDto, requesterId);
        return itemRequestClient.addItemRequest(requesterId, itemRequestInputDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable(value = "requestId") long requestId
    ) {
        log.info("GW receive GET request for return itemRequest by id={}", requestId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping("")
    public ResponseEntity<Object> getAllRequestersItemRequests(
            @RequestHeader(value = "X-Sharer-User-Id") long requesterId,
            @RequestParam(value = "from", defaultValue = DEFAULT_FROM) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) @Positive int size
    ) {
        log.info("GW receive GET request for return all items of requesterId={} from={} size={}",
                requesterId, from, size);
        return itemRequestClient.getAllRequestersItemRequests(requesterId, from, size);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = DEFAULT_FROM) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) @Positive int size
    ) {
        log.info("GW receive GET request for return all items of userId={} from={} size={}", userId, from, size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

}
