package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryStorage<T extends Item> implements Storage<T> {

    private final Map<Long,T> storage;
    private Long globalId;

    public InMemoryStorage() {
        storage = new HashMap<>();
        globalId = 1L;
    }

    public T add(T item) {
        item.setId(globalId);
        storage.put(globalId,item);
        globalId++;
        return item;
    }

    public void delete(Long id) {
        storage.remove(id);
    }

    public T update(T item) {
        if (contains(item.getId())) {
            storage.put(item.getId(),item);
        } else {
            throw new NotFoundException("item with id doesn't exist - " + item.getId());
        }
        return item;
    }

//    public boolean contains(T item) {
//        return storage.containsKey(item.getId());
//    }

    public boolean contains(Long id) {
        return storage.containsKey(id);
    }

    public T getItem(Long id) {
        return storage.get(id);
    }

    public List<T> getAllItemsList() {
        return new ArrayList<>(storage.values());
    }
}
