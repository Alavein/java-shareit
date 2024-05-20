package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    public Item createItem(Item item);

    public Item getItemById(long itemId);

    public List<Item> getItemsByUser(long id);

    public Item updateItem(long id, Item item);

    public List<Item> searchItems(String text);

    public void deleteItem(long id);
}