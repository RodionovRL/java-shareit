package ru.practicum.shareit.item.comment.repositiry.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.api.ItemRepository;
import ru.practicum.shareit.request.repository.api.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.api.UserRepository;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    ItemRequestRepository requestRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;

    @Test
    void findAllInItemId() {

        User requester = User.builder()
                .name("Леопольд")
                .email("cat@mail.ru")
                .build();
        User notRequester = User.builder()
                .name("Винни")
                .email("pooh@mail.ru")
                .build();
        User owner1 = User.builder()
                .name("Попугай")
                .email("kesha@mail.ru")
                .build();
        User owner2 = User.builder()
                .name("Биба")
                .email("boba@mail.ru")
                .build();

        requester = userRepository.save(requester);
        notRequester =  userRepository.save(notRequester);
        owner1 = userRepository.save(owner1);
        owner2 = userRepository.save(owner2);

        Item item1 = new Item(0L, "item1", "description1", true, owner1, null);
        Item item2 = new Item(0L, "item2", "description2", true, owner2, null);
        Item item3 = new Item(0L, "item3", "description3", true, owner2, null);
        Item item4 = new Item(0L, "item4", "description4", true, owner1, null);

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);
        item3 = itemRepository.save(item3);
        item4 = itemRepository.save(item4);

        Comment comment1 = new Comment(0L, "comment1", now(), item1, requester);
        Comment comment2 = new Comment(0L, "comment2", now(), item2, notRequester);
        Comment comment3 = new Comment(0L, "comment3", now(), item3, owner2);
        Comment comment4 = new Comment(0L, "comment4", now(), item4, requester);

        Comment requestComment1 = commentRepository.save(comment1);
        Comment requestComment3 = commentRepository.save(comment3);
        Comment requestComment4 = commentRepository.save(comment4);


        List<Long> itemsId = List.of(item1.getId(), item3.getId(), item4.getId());
        List<Comment> allInItemId1 = commentRepository.findAllInItemId(itemsId);

        assertAll(
                () -> assertNotNull(allInItemId1),
                () -> assertEquals(3, allInItemId1.size()),
                () -> assertThat(allInItemId1, hasItem(requestComment1)),
                () -> assertThat(allInItemId1, hasItem(requestComment3)),
                () -> assertThat(allInItemId1, hasItem(requestComment4))
                );
    }
}