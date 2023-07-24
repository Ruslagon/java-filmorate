package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
@Component
@Qualifier("inMemoryUser")
public class InMemoryUserStorage extends InMemoryStorage<User> implements UserStorage {
}
