package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Qualifier("FilmDb")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film item) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("PUBLIC.FILM")
                .usingColumns("FILM_NAME", "DESCRIPTION", "RELEASE_DATE", "DURATION", "RATING_ID")
                .usingGeneratedKeyColumns("FILM_ID");
        item.setId(simpleJdbcInsert.executeAndReturnKey(item.toMap()).longValue());
        for (Genre genre : item.getGenres()) {
            jdbcTemplate.update("INSERT INTO PUBLIC.FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)",item.getId(),genre.getId());
//            SimpleJdbcInsert jdbcInsertGenre = new SimpleJdbcInsert(jdbcTemplate)
//                    .usingColumns("FILM_ID", "GENRE_ID").withTableName("PUBLIC.FILM_GENRE");
//            jdbcInsertGenre.execute(genre.genresToMapBd(item.getId()));
        }
        return item;
    }

    @Override
    public Film delete(Film item) {
        jdbcTemplate.update("DELETE FROM PUBLIC.FILM WHERE FILM_ID = ?", item.getId());
        return item;
    }

    @Override
    public Film update(Film item) {
        String sqlQuery = "UPDATE PUBLIC.FILM SET " +
                "FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? WHERE FILM_ID = ?";

        Set<Genre> newSetGenres = item.getGenres();
        Set<Genre> oldSetGenres = getGenres(item.getId());
//        List<Genre> newGenres = newSetGenres.stream().filter(genre -> !oldSetGenres.contains(genre)).collect(Collectors.toList());
//        List<Genre> oldLostGenres = oldSetGenres.stream().filter(genre -> !newSetGenres.contains(genre)).collect(Collectors.toList());
        for (Genre genre: oldSetGenres) {
            jdbcTemplate.update("DELETE FROM PUBLIC.FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?", item.getId(), genre.getId());
        }
        for (Genre genre: newSetGenres) {
            jdbcTemplate.update("INSERT INTO PUBLIC.FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)", item.getId(),genre.getId());
        }
        Set<Long> newLikedList = item.getLikesIds();
        Set<Long> oldLikedList = getLikedList(item.getId());
        for (Long oldLike: oldLikedList) {
            jdbcTemplate.update("DELETE FROM PUBLIC.FILMS_LIKES WHERE FILM_ID = ? AND USER_ID = ?", item.getId(), oldLike);
        }
        for (Long newLike: newLikedList) {
            jdbcTemplate.update("INSERT INTO PUBLIC.FILMS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)", item.getId(),newLike);
        }
        if (contains(item.getId())) {
            jdbcTemplate.update(sqlQuery, item.getName(), item.getDescription(), item.getReleaseDate(), item.getDuration(),item.getMpa().getId(), item.getId());
        } else {
            throw new NotFoundException("item with id doesn't exist - " + item.getId());
        }
        return item;
    }

    private Set<Long> getLikedList(long id) {
        String sqlQueryForLikes = "SELECT USER_ID FROM FILMS_LIKES WHERE FILM_ID = ?";
        Set<Long> likes = new HashSet<>();
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sqlQueryForLikes, id);
        while (likesRows.next()) {
            likes.add(likesRows.getLong("GENRE_ID"));
        }
        return likes;
    }

    private Set<Genre> getGenres(long id) {
        String sqlQueryForGenres = "SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?";
        Set<Genre> genres = new HashSet<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQueryForGenres, id);
        while (genreRows.next()) {
            Genre genre = new Genre();
            genre.setId(genreRows.getInt("GENRE_ID"));
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public boolean contains(Long id) {
        String sqlQuery = "SELECT FILM_ID FROM PUBLIC.FILM WHERE FILM_ID = ?";
        return jdbcTemplate.queryForRowSet(sqlQuery, id).next();
    }

    @Override
    public Film getItem(Long id) {
        String sqlQueryForGenres = "SELECT GENRE_ID FROM PUBLIC.FILM_GENRE WHERE FILM_ID = ?";
        String sqlQueryForLikes = "SELECT USER_ID FROM PUBLIC.FILMS_LIKES WHERE FILM_ID = ?";
        String sqlQuery = "SELECT FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID" +
                " FROM PUBLIC.FILM WHERE FILM_ID = ?";
        Set<Genre> genres = new TreeSet<>((Genre g1, Genre g2) -> g1.getId() - g2.getId());
        Set<Long> likes = new HashSet<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQueryForGenres,id);
        while (genreRows.next()) {
            Genre genre = new Genre();
            genre.setId(genreRows.getInt("GENRE_ID"));
            genres.add(genre);
        }
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sqlQueryForLikes,id);
        while (likesRows.next()) {
            likes.add(likesRows.getLong("USER_ID"));
        }
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        film.setLikesIds(likes);
        film.setGenres(genres);
        return film;
    }

    @Override
    public List<Film> getAllItemsList() {
        List<Film> films = new ArrayList<>();
        String sqlQuery = "SELECT FILM_ID FROM PUBLIC.FILM";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (userRows.next()) {
            films.add(getItem(userRows.getLong("FILM_ID")));
        }
        return films;
    }

    private Film mapRowToFilm(ResultSet rs, int rn) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("RATING_ID"));
        Film film = new Film();
        film.setId(rs.getLong("FILM_ID"));
        film.setName(rs.getString("FILM_NAME"));
        film.setDescription(rs.getString("DESCRIPTION"));
        film.setReleaseDate(rs.getDate("RELEASE_DATE"));
        film.setDuration(rs.getInt("DURATION"));
        film.setMpa(mpa);
        return film;
    }
}
