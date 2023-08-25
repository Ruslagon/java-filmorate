package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Component
@Qualifier("FilmDb")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final LikesDbStorage likesDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage, LikesDbStorage likesDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
        this.likesDbStorage = likesDbStorage;
    }

    @Override
    public Film add(Film item) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("PUBLIC.FILM")
                .usingColumns("FILM_NAME", "DESCRIPTION", "RELEASE_DATE", "DURATION", "RATING_ID")
                .usingGeneratedKeyColumns("FILM_ID");
        item.setId(simpleJdbcInsert.executeAndReturnKey(item.toMap()).longValue());
        genreDbStorage.saveGenres(item);
        return item;
    }

    @Override
    public void delete(Long id) {
        if (contains(id)) {
            jdbcTemplate.update("DELETE FROM PUBLIC.FILM WHERE FILM_ID = ?", id);
        }
    }

    @Override
    public Film update(Film item) {
        String sqlQuery = "UPDATE PUBLIC.FILM SET " +
                "FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? WHERE FILM_ID = ?";
        if (contains(item.getId())) {
            jdbcTemplate.update(sqlQuery, item.getName(), item.getDescription(), item.getReleaseDate(), item.getDuration(),item.getMpa().getId(), item.getId());
        } else {
            throw new NotFoundException("item with id doesn't exist - " + item.getId());
        }
        genreDbStorage.saveGenres(item);
        likesDbStorage.saveLikes(item);
        return item;
    }

    @Override
    public boolean contains(Long id) {
        String sqlQuery = "SELECT FILM_ID FROM PUBLIC.FILM WHERE FILM_ID = ?";
        return jdbcTemplate.queryForRowSet(sqlQuery, id).next();
    }

    @Override
    public Film getItem(Long id) {
        final String sqlQuery = "SELECT * FROM PUBLIC.FILM f, PUBLIC.MPA m where f.RATING_ID = m.RATING_ID AND FILM_ID = ?";
        if (contains(id)) {
            final Film film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
            film.setGenres(genreDbStorage.getGenres(id));
            film.setLikesIds(likesDbStorage.getLikedList(id));
            return film;
        } else {
            throw new NotFoundException("не найден фильм с id - " + id);
        }
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("FILM_ID"));
        film.setName(rs.getString("FILM_NAME"));
        film.setDescription(rs.getString("DESCRIPTION"));
        film.setReleaseDate((rs.getDate("RELEASE_DATE")).toLocalDate());
        film.setDuration(rs.getInt("DURATION"));
        film.setMpa(new Mpa(rs.getInt("MPA.RATING_ID"), rs.getString("MPA.RATING")));
        return film;
    }

    @Override
    public List<Film> getAllItemsList() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM PUBLIC.FILM f, PUBLIC.MPA m where f.RATING_ID = m.RATING_ID", this::makeFilm);
        Map<Long, TreeSet<Genre>> allGenres = genreDbStorage.getAllGenres();
        Map<Long, Set<Long>> allLikes = likesDbStorage.getAllLikes();
        for (Film film : films) {
            if (allGenres.containsKey(film.getId())) {
                film.setGenres(allGenres.get(film.getId()));
            }
        }
        for (Film film : films) {
            if (allLikes.containsKey(film.getId())) {
                film.setLikesIds(allLikes.get(film.getId()));
            }
        }
        return films;
    }

    public void updateFilm(Film film) {
        String sqlQuery = "update FILM SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        genreDbStorage.saveGenres(film);
    }
}
