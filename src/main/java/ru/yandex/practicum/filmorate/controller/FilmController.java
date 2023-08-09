package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
public class FilmController {
    Logger log = (Logger) LoggerFactory.getLogger(FilmController.class);
    final private Map<Integer, Film> films = new HashMap<>();
    final private LocalDate earliestFilmDate = LocalDate.of(1895,12,28);
    private int id = 1;

    @GetMapping("/films")
    public List<Film> findAll(){
        return new ArrayList<Film>(films.values());
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        if (validation(film)) {
            log.warn("данные фильма не подходят формату");
            throw new ValidationException("фильм имеет неверные данные");
        }
        film.setId(id);
        films.put(id,film);
        id++;
        log.info("добавлен фильм: {}", film.toString());
        return film;
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        if (validation(film) || !films.containsKey(film.getId())) {
            log.warn("неверные данные фильма");
            throw new ValidationException("фильм имеет неверные данные или фильма нет в библиотеке");
        }
        films.put(film.getId(),film);
        log.info("данные фильма обновлены : {}", film.toString());
        return film;
    }

    public boolean validation(Film film) {
        return film.getDescription().length() > 200 || film.getReleaseDate().isBefore(earliestFilmDate) || film.getDuration() <= 0
                || film.getName().isEmpty() || film.getName().isBlank();
    }
}
