package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    public Item createItem(Item item);

    public Item getItemById(long id);

    public List<Item> getItemsByUser(long id);

    public Item updateItem(long id, Item item);

    public void deleteItem(long id);
}