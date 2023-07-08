package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@RestController
public class FilmController {
    Logger log = (Logger) LoggerFactory.getLogger(FilmController.class);
    final private Set<Film> films = new HashSet<>();
    final private LocalDate movieDate = LocalDate.of(1895,12,28);
    private int id = 1;

    @GetMapping("/films")
    public Set<Film> findAll(){
        return films;
    }

    @PostMapping("/films")
    public Film create(@RequestBody Film film) throws ValidationException {
        if (validation(film)) {
            log.setLevel(Level.WARN);
            log.warn("данные фильма не подходят формату");
            throw new ValidationException("фильм имеет неверные данные");
        }
        film.setId(id);
        id++;
        films.add(film);
        log.setLevel(Level.INFO);
        log.info("добавлен фильм: {}", films.toString());
        return film;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) throws ValidationException {
        if (validation(film) || !films.contains(film)) {
            log.setLevel(Level.WARN);
            log.warn("неверные данные фильма");
            throw new ValidationException("фильм имеет неверные данные или фильма нет в библиотеке");
        }
        films.remove(film);
        films.add(film);
        log.setLevel(Level.INFO);
        log.info("данные фильма обновлены : {}", film.toString());
        return film;
    }

    private boolean validation(Film film) {
        return film.getName() == null || film.getName().isBlank() || film.getDescription().length() > 200 ||
                !film.getReleaseDate().isAfter(movieDate) || film.getDuration() <= 0;
    }
}
