package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class FilmService {

    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private UserStorage userStorage;

    public List<Film> findAll() {
        return filmStorage.getAllItemsList();
    }

    public Film create(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getMovieById(Long id) {
        if (!filmStorage.contains(id)) {
            throw new NotFoundException("Этого фильма не существует" + id);
        }
        return filmStorage.getItem(id);
    }

    public Set<Long> likeFilm(Long filmId, Long userId) {
        if (!filmStorage.contains(filmId)) {
            throw new NotFoundException("this id doesn't exist - " + filmId);
        }
        if (!userStorage.contains(userId)) {
            throw new NotFoundException("this id doesn't exist - " + userId);
        }
        Film film = filmStorage.getItem(filmId);
        film.addLike(userId);
        userStorage.getItem(userId).addLikedFilm(filmId);
        return film.getLikesIds();
    }

    public Set<Long> deleteFilmsLike(Long filmId, Long userId) {
        if (!filmStorage.contains(filmId)) {
            throw new NotFoundException("this id doesn't exist - " + filmId);
        }
        if (!userStorage.contains(userId)) {
            throw new NotFoundException("this id doesn't exist - " + userId);
        }
        Film film = filmStorage.getItem(filmId);
        film.deleteLike(userId);
        userStorage.getItem(userId).deleteLikedFilm(filmId);
        return film.getLikesIds();
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getAllItemsList().stream().sorted((f1,f2) -> {
            int f1LikesCount = f1.getCountLike();
            int f2LikesCount = f2.getCountLike();
            int comp = f2LikesCount - f1LikesCount;
            if (comp == 0) {
                return f2.getId().compareTo(f1.getId());
            }
            return comp;
        }).limit(count).collect(Collectors.toList());
    }

    public Set<Mpa> getAllMpa() {
        Set<Mpa> mpaSet = new TreeSet<>((Mpa m1, Mpa m2) -> m1.getId() - m2.getId());
        for (int i = 1; i < 6; i++) {
            mpaSet.add(getMpaById(i));
        }
        return mpaSet;
    }

    public Mpa getMpaById(@PathVariable int id) {
        if (id > 5) {
            throw new NotFoundException("не существует mpa с индексом - " + id);
        }
        Mpa mpa = new Mpa();
        mpa.setId(id);
        return mpa;
    }

    public Set<Genre> getAllGenre() {
        Set<Genre> genreSet = new TreeSet<>((Genre m1, Genre m2) -> m1.getId() - m2.getId());
        for (int i = 1; i < 7; i++) {
            genreSet.add(getGenreById(i));
        }
        return genreSet;
    }

    public Genre getGenreById(@PathVariable int id) {
        if (id > 6) {
            throw new NotFoundException("не существует mpa с индексом - " + id);
        }
        Genre genre = new Genre();
        genre.setId(id);
        return genre;
    }
}
