package ru.practicum.shareit.item;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Component
public class InMemoryItemRepository implements ItemRepository {

    private long id = 1;
    private final HashMap<Long, Item> items = new HashMap<>();

    private long getNextId() {
        return id++;
    }

    @Override
    public Item createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItem(long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new ItemNotFoundException(String.format("Ошибка. Вещь с id = %d не найдена.", id));
        }
    }

    @Override
    public List<Item> getItemsByUser(long id) {
        return items.values().stream()
                .filter(item -> item.getOwner() == id)
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(long id, Item item) {
        if (item.getOwner() == items.get(id).getOwner()) {
            if (item.getName() != null)
                items.get(id).setName(item.getName());
            if (item.getDescription() != null)
                items.get(id).setDescription(item.getDescription());
            if (item.getAvailable() != null)
                items.get(id).setAvailable(item.getAvailable());
        } else {
            throw new ItemNotFoundException(
                    String.format("Вещь с id = %d владельца с id = %d не найдена", id, item.getOwner()));
        }
        return items.get(id);
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(i -> (i.getDescription().toLowerCase().contains(text.toLowerCase())
                        || i.getName().toLowerCase().contains(text.toLowerCase())) && i.getAvailable())
                .collect(Collectors.toList());
    }
}
