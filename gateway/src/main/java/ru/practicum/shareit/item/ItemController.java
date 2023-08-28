package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    public final ItemClient itemClient;
    private static final String DEFAULT_SIZE = "25";
    private static final String DEFAULT_FROM = "0";

    @PostMapping
    public ResponseEntity<Object> postItem(
            @RequestHeader(value = "X-Sharer-User-Id") long ownerId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        log.info("GW receive POST request for add new item with body={}, ownerId={}", itemDto, ownerId);
        return itemClient.addItem(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable(value = "itemId") long itemId,
            @Valid @RequestBody CommentInputDto commentInputDto
    ) {
        log.info("GW receive POST request for add new comment with body={}, itemId={} , userId={}",
                commentInputDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentInputDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(value = "X-Sharer-User-Id") long ownerId,
            @PathVariable(value = "id") long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("GW receive PATCH request for update item with id={}, requestBody={}, ownerId={}",
                itemId, itemDto, ownerId);
        return itemClient.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable(value = "id") long id
    ) {
        log.info("GW receive GET request for return item by id={} for userId={}", id, userId);
        return itemClient.getItemById(userId, id);
    }

    @GetMapping("")
    public ResponseEntity<Object> getAllOwnersItems(
            @RequestHeader(value = "X-Sharer-User-Id") long ownerId,
            @RequestParam(value = "from", defaultValue = DEFAULT_FROM) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) @Positive int size
    ) {
        log.info("GW receive GET request for return all items of ownerId={} from={} size={}", ownerId, from, size);
        return itemClient.getAllOwnersItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItems(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestParam(value = "text") String text,
            @RequestParam(value = "from", defaultValue = DEFAULT_FROM) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) @Positive int size
    ) {
        log.info("GW receive GET to find item by text={} from={} size={}", text, from, size);
        return itemClient.findItems(userId, text, from, size);
    }
}
