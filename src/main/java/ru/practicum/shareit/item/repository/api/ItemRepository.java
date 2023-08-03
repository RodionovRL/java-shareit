package ru.practicum.shareit.item.repository.api;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerOrderById(User owner);

    List<Item> findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableIsOrderById(String text1,
                                                                                     String text2,
                                                                                     boolean isAvailable);
}
