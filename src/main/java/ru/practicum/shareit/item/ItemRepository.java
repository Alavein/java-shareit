package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    public Item createItem(Item item);

    public Item getItem(long id);

    public List<Item> getItemsByUser(long id);

    public Item updateItem(long id, Item item);

    public List<Item> searchItems(String text);

    public void deleteItem(long id);

}
