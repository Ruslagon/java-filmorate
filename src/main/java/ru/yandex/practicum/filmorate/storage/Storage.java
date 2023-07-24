package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<Item> {
    Item add(Item item);
    Item delete(Item item);
    Item update(Item item);
    boolean contains(Long id);

    Item getItem(Long id);
    List<Item> getAllItemsList();
}
