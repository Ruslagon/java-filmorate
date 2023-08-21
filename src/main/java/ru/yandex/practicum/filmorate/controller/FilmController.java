package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@Validated
@RequestMapping
public class FilmController {
    private final LocalDate earliestFilmDate = LocalDate.of(1895,12,28);
    @Autowired
    private FilmService filmService;

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        log.info("добавлен фильм: {}", film.toString());
        return filmService.create(film);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        log.info("данные фильма обновлены : {}", film.toString());
        return filmService.update(film);
    }

    @GetMapping("/films/{id}")
    public Film findOne(@PathVariable Long id) {
        if (idValidation(id)) {
            throw new NotFoundException("id фильма введено неверно - " + id);
        }
        return filmService.getMovieById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Set<Long> likeFilm(@PathVariable(value = "id") Long filmId, @PathVariable Long userId) {
        if (idValidation(filmId)) {
            throw new NotFoundException("id фильма введено неверно - " + filmId);
        }
        if (idValidation(userId)) {
            throw new NotFoundException("id пользователя введено неверно - " + filmId);
        }
        return filmService.likeFilm(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Set<Long> deleteLike(@PathVariable(value = "id") Long filmId, @PathVariable Long userId) {
        if (idValidation(filmId)) {
            throw new NotFoundException("id фильма введено неверно - " + filmId);
        }
        if (idValidation(userId)) {
            throw new NotFoundException("id пользователя введено неверно - " + userId);
        }
        return filmService.deleteFilmsLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostPopularFilms(@RequestParam(value = "count", defaultValue = "10")@Positive int count) {
        if (idValidation((long)count)) {
            throw new ValidationException("count введен неверно -" + count);
        }
        return filmService.getMostPopularFilms(count);
    }

    @GetMapping("/mpa")
    public Set<Mpa> getAllMpa() {
        return filmService.getAllMpa();
    }
    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        return filmService.getMpaById(id);
    }

    @GetMapping("/genres")
    public Set<Genre> getAllGenre() {
        return filmService.getAllGenre();
    }
    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return filmService.getGenreById(id);
    }

    public boolean idValidation(Long id) {
        return (id == null || id <= 0);
    }
}
