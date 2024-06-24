package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User user;
    private Item item1;
    private Item item2;
    private ItemRequest itemRequest;


    @BeforeEach
    void add() {
        user = new User(null, "Имя", "mail@yandex.ru");
        userRepository.save(user);

        item1 = new Item(null, "Предмет", "Описание", true, user,
                null);
        itemRepository.save(item1);
        item2 = new Item(null, "Предмет2", "Описание", true, user,
                null);
        itemRepository.save(item2);

        itemRequest = new ItemRequest(null, "Описание", user, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
    }

    @Test
    void getItems() {
        Boolean available = true;
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> listItem = List.of(item1, item2);

        List<Item> item1 = itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable("Предмет",
                        "Предмет", available, pageable);
        assertEquals(listItem, item1);
        assertEquals(2, item1.size());

        List<Item> item2 = itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable("ПРЕДМЕТ",
                        "ПРЕДМЕТ", available, pageable);
        assertEquals(listItem, item2);
        assertEquals(2, item2.size());


        List<Item> item3 = itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable("Описание",
                        "Описание", available, pageable);
        assertEquals(listItem, item3);
        assertEquals(2, item3.size());

        List<Item> item4 = itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable("Имя",
                        "Имя", available, pageable);
        assertEquals(List.of(), item4);
    }

    @Test
    void getItemsOfUser() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Item> listItem = List.of(item1, item2);

        List<Item> item1 = itemRepository
                .findAllByOwnerId(user.getId(), pageable);
        assertEquals(listItem, item1);
        assertEquals(2, item1.size());

    }
}
