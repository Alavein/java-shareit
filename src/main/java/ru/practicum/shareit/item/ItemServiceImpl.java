package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item createItem(Item item) {
        log.info("Создание новой вещи = {}", item);
        userRepository.getUserById(item.getOwner());
        return itemRepository.createItem(item);
    }

    @Override
    public Item getItemById(long id) {
        log.info("Получние информации о вещи с id = {}", id);
        return itemRepository.getItemById(id);
    }

    @Override
    public List<Item> getItemsByUser(long id) {
        log.info("Получение вещей пользователя с id = {}", id);
        return itemRepository.getItemsByUser(id);
    }

    @Override
    public Item updateItem(long id, Item item) {
        log.info("Обновление информации о вещи = {} с id = {}", item, id);
        return itemRepository.updateItem(id, item);
    }

    @Override
    public List<Item> searchItems(String text) {
        log.info("Поиск вещи по ключевым словам = {}", text);
        if (text.isEmpty())
            return new ArrayList<>();
        return itemRepository.searchItems(text);
    }

    @Override
    public void deleteItem(long id) {
        itemRepository.deleteItem(id);
    }
}