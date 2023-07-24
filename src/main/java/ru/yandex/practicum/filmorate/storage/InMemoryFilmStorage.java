package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
@Component
@Qualifier("inMemoryFilm")
public class InMemoryFilmStorage extends InMemoryStorage<Film> implements FilmStorage {

}
