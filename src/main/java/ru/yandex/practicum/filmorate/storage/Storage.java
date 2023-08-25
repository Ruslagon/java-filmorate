package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {

    T add(T t);

    void delete(Long id);

    T update(T t);

    boolean contains(Long id);

    T getItem(Long id);

    List<T> getAllItemsList();
}
