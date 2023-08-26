package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.service.api.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    public static final String DEFAULT_FROM = "0";
    public static final String DEFAULT_SIZE = "25";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestOutDto> postItemRequest(
            @Valid @RequestBody ItemRequestInputDto itemRequestInputDto,
            @RequestHeader(value = "X-Sharer-User-Id") long requesterId
    ) {
        log.info("receive POST request for add new itemRequest with body={}, requesterId={}",
                itemRequestInputDto, requesterId);
        ItemRequestOutDto savedItemRequestOutDto = itemRequestService.addItemRequest(itemRequestInputDto, requesterId);
        return new ResponseEntity<>(savedItemRequestOutDto, HttpStatus.CREATED);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestOutDto> getItemRequestById(
            @PathVariable(value = "requestId") long requestId,
            @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        log.info("receive GET request for return itemRequest by id={}", requestId);
        ItemRequestOutDto itemRequestOutDto = itemRequestService.getItemRequestById(requestId, userId);
        return new ResponseEntity<>(itemRequestOutDto, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<ItemRequestOutDto>> getAllRequestersItemRequests(
            @RequestParam(value = "from", defaultValue = DEFAULT_FROM) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) @Positive int size,
            @RequestHeader(value = "X-Sharer-User-Id") long requesterId
    ) {
        log.info("receive GET request for return all items of requesterId={} from={} size={}", requesterId, from, size);
        List<ItemRequestOutDto> itemRequestOutputDto =
                itemRequestService.getAllRequestersItemRequests(requesterId, from, size);
        return new ResponseEntity<>(itemRequestOutputDto, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestOutDto>> getAllItemRequests(
            @RequestParam(value = "from", defaultValue = DEFAULT_FROM) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) @Positive int size,
            @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        log.info("receive GET request for return all items of userId={} from={} size={}", userId, from, size);
        List<ItemRequestOutDto> itemRequestOutputDto =
                itemRequestService.getAllItemRequests(userId, from, size);
        return new ResponseEntity<>(itemRequestOutputDto, HttpStatus.OK);
    }
}
