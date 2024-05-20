package ru.practicum.shareit.item;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Component
public class InMemoryItemRepository implements ItemRepository {

    private long id = 1;
    private final HashMap<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        long counter = ++id;
        item.setId(counter);
        items.put(counter, item);
        return item;
    }

    @Override
    public Item getItemById(long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        }
        return null;
    }

    @Override
    public List<Item> getItemsByUser(long id) {
        return items.values().stream()
                .filter(item -> item.getOwner() == id)
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(long id, Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public void deleteItem(long id) {
        items.remove(id);
    }

    private long getNextId() {
        return id++;
    }
}
