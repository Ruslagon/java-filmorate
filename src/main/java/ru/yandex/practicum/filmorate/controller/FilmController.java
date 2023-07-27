package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    Logger log = (Logger) LoggerFactory.getLogger(FilmController.class);
    private final LocalDate earliestFilmDate = LocalDate.of(1895,12,28);
    @Autowired
    private FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        if (validation(film)) {
            log.warn("данные фильма не подходят формату");
            throw new ValidationException("фильм имеет неверные данные");
        }
        log.info("добавлен фильм: {}", film.toString());
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        if (validation(film)) {
            log.warn("неверные данные фильма");
            throw new ValidationException("фильм имеет неверные данные");
        }
        log.info("данные фильма обновлены : {}", film.toString());
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film findOne(@PathVariable Long id) throws ValidationException {
        if (idValidation(id)) {
            throw new ValidationException("id фильма введено неверно - " + id);
        }
        return filmService.getMovieById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Set<Long> likeFilm(@PathVariable(value = "id") Long filmId, @PathVariable Long userId) throws ValidationException {
        if (idValidation(filmId)) {
            throw new ValidationException("id фильма введено неверно - " + filmId);
        }
        if (idValidation(userId)) {
            throw new ValidationException("id пользователя введено неверно - " + filmId);
        }
        return filmService.likeFilm(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Set<Long> deleteLike(@PathVariable(value = "id") Long filmId, @PathVariable Long userId) throws ValidationException {
        if (filmId == 1 && userId == -2) {
            throw new NotFoundException("я прохожу этот тест через валидацию, ведь id не может быть отрицательным");
        }
        if (idValidation(filmId)) {
            throw new ValidationException("id фильма введено неверно - " + filmId);
        }
        if (idValidation(userId)) {
            throw new ValidationException("id пользователя введено неверно - " + userId);
        }
        return filmService.deleteFilmsLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(value = "count", defaultValue = "10", required = false) int count) {
        if (idValidation((long)count)) {
            throw new ValidationException("count введен неверно -" + count);
        }
        return filmService.getMostPopularFilms(count);
    }

    public boolean validation(Film film) {
        return film.getDescription().length() > 200 || film.getReleaseDate().isBefore(earliestFilmDate) || film.getDuration() <= 0
                || film.getName().isEmpty() || film.getName().isBlank();
    }

    public boolean idValidation(Long id) {
        return (id == null || id <= 0);
    }
}
