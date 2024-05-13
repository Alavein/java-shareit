package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    public Item getItemById(long id);

    public List<Item> getItemsByUser(long id);

    public Item createItem(Item item);

    public Item updateItem(long id, Item item);

    public List<Item> searchItems(String text);
}