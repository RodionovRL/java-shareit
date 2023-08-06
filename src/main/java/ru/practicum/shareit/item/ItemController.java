package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentInputDto;
import ru.practicum.shareit.item.comment.dto.SavedCommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsOutputDto;
import ru.practicum.shareit.item.service.api.ItemService;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> postItem(@Valid @RequestBody ItemDto itemDto,
                                            @RequestHeader(value = "X-Sharer-User-Id") long ownerId
    ) {
        log.info("receive POST request for add new item with body={}, ownerId={}", itemDto, ownerId);
        ItemDto savedItemDto = itemService.addItem(itemDto, ownerId);
        return new ResponseEntity<>(savedItemDto, HttpStatus.CREATED);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<SavedCommentOutputDto> postComment(@Valid @RequestBody CommentInputDto commentInputDto,
                                                             @PathVariable(value = "itemId") long itemId,
                                                             @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("receive POST request for add new comment with body={}, itemId={} , userId={}",
                commentInputDto,
                itemId,
                userId);
        SavedCommentOutputDto newSavedCommentOutputDto = itemService.addComment(commentInputDto, itemId, userId);
        return new ResponseEntity<>(newSavedCommentOutputDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto,
                                              @PathVariable(value = "id") long itemId,
                                              @RequestHeader(value = "X-Sharer-User-Id") long ownerId
    ) {
        log.info("receive PATCH request for update item with id={}, requestBody={}, ownerId={}",
                itemId, itemDto, ownerId);
        ItemDto updatedItemDto = itemService.updateItem(itemId, itemDto, ownerId);
        return new ResponseEntity<>(updatedItemDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemWithCommentsOutputDto> getItemById(@PathVariable(value = "id") long id,
                                                                 @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        log.info("receive GET request for return item by id={}", id);
        ItemWithCommentsOutputDto itemWithCommentsOutputDto = itemService.getItemById(id, userId);
        return new ResponseEntity<>(itemWithCommentsOutputDto, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<ItemWithCommentsOutputDto>> getAllOwnersItems(@RequestHeader(value = "X-Sharer-User-Id") long ownerId
    ) {
        log.info("receive GET request for return all items of ownerId={}", ownerId);
        List<ItemWithCommentsOutputDto> itemWithCommentsOutputDto = itemService.getAllOwnersItems(ownerId);
        return new ResponseEntity<>(itemWithCommentsOutputDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> findItems(@RequestParam(value = "text") String text
    ) {
        log.info("receive GET to find item by text={}", text);
        List<ItemDto> itemsDto = itemService.findItems(text);
        return new ResponseEntity<>(itemsDto, HttpStatus.OK);
    }
}
