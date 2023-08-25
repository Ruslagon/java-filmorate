package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
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
        saveGenres(item);
//        for (Genre genre : item.getGenres()) {
//            jdbcTemplate.update("INSERT INTO PUBLIC.FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)",item.getId(),genre.getId());
////            SimpleJdbcInsert jdbcInsertGenre = new SimpleJdbcInsert(jdbcTemplate)
////                    .usingColumns("FILM_ID", "GENRE_ID").withTableName("PUBLIC.FILM_GENRE");
////            jdbcInsertGenre.execute(genre.genresToMapBd(item.getId()));
//        }
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
//        Set<Genre> newSetGenres = item.getGenres();
//        Set<Genre> oldSetGenres = getGenres(item.getId());
////        List<Genre> newGenres = newSetGenres.stream().filter(genre -> !oldSetGenres.contains(genre)).collect(Collectors.toList());
////        List<Genre> oldLostGenres = oldSetGenres.stream().filter(genre -> !newSetGenres.contains(genre)).collect(Collectors.toList());
//        for (Genre genre: oldSetGenres) {
//            jdbcTemplate.update("DELETE FROM PUBLIC.FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?", item.getId(), genre.getId());
//        }
//        for (Genre genre: newSetGenres) {
//            jdbcTemplate.update("INSERT INTO PUBLIC.FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)", item.getId(),genre.getId());
//        }
          saveGenres(item);
//        Set<Long> newLikedList = item.getLikesIds();
//        Set<Long> oldLikedList = getLikedList(item.getId());
//        for (Long oldLike: oldLikedList) {
//            jdbcTemplate.update("DELETE FROM PUBLIC.FILMS_LIKES WHERE FILM_ID = ? AND USER_ID = ?", item.getId(), oldLike);
//        }
//        for (Long newLike: newLikedList) {
//            jdbcTemplate.update("INSERT INTO PUBLIC.FILMS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)", item.getId(),newLike);
//        }
        saveLikes(item);
        return item;
    }

    private Set<Long> getLikedList(long id) {
        String sqlQueryForLikes = "SELECT USER_ID FROM PUBLIC.FILMS_LIKES WHERE FILM_ID = ?";
        Set<Long> likes = new HashSet<>();
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sqlQueryForLikes, id);
        while (likesRows.next()) {
            Long userId = likesRows.getLong("USER_ID");
            likes.add(userId);
        }
        return likes;
    }

    private Set<Genre> getGenres(long id) {
        String sqlQueryForGenres = "SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?";
        Set<Genre> genres = new TreeSet<>((Genre g1, Genre g2) -> g1.getId() - g2.getId());
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
        final String sqlQuery = "SELECT * FROM PUBLIC.FILM f, PUBLIC.MPA m where f.RATING_ID = m.RATING_ID AND FILM_ID = ?";
        if (contains(id)) {
            final Film film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
            film.setGenres(getGenres(id));
            film.setLikesIds(getLikedList(id));
            return film;
        } else {
            throw new NotFoundException("не найден фильм с id - " + id);
        }
//
//        String sqlQueryForGenres = "SELECT GENRE_ID FROM PUBLIC.FILM_GENRE WHERE FILM_ID = ?";
//        String sqlQueryForLikes = "SELECT USER_ID FROM PUBLIC.FILMS_LIKES WHERE FILM_ID = ?";
//        String sqlQuery = "SELECT FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID" +
//                " FROM PUBLIC.FILM WHERE FILM_ID = ?";
//        Set<Genre> genres = new TreeSet<>((Genre g1, Genre g2) -> g1.getId() - g2.getId());
//        Set<Long> likes = new HashSet<>();
//        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQueryForGenres,id);
//        while (genreRows.next()) {
//            Genre genre = new Genre();
//            genre.setId(genreRows.getInt("GENRE_ID"));
//            genres.add(genre);
//        }
//        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sqlQueryForLikes,id);
//        while (likesRows.next()) {
//            likes.add(likesRows.getLong("USER_ID"));
//        }
//        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
//        film.setLikesIds(likes);
//        film.setGenres(genres);
//        return film;
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
        Map<Long, TreeSet<Genre>> allGenres = getAllGenres();
        Map<Long, Set<Long>> allLikes = getAllLikes();
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
//        List<Film> films = new ArrayList<>();
//        String sqlQuery = "SELECT FILM_ID FROM PUBLIC.FILM";
//        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery);
//        while (userRows.next()) {
//            films.add(getItem(userRows.getLong("FILM_ID")));
//        }
//        return films;
    }

    private Map<Long, TreeSet<Genre>> getAllGenres() {
        Map<Long, TreeSet<Genre>> allGenres = new HashMap<>();
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet("SELECT FILM_ID, GENRE_ID FROM PUBLIC.FILM_GENRE");
        while (genresRows.next()) {
            int newGenreId = genresRows.getInt("GENRE_ID");
            Long filmId = genresRows.getLong("FILM_ID");
            TreeSet<Genre> genres = new TreeSet<>((Genre g1, Genre g2) -> g1.getId() - g2.getId());
            Genre newGenre = new Genre();
            newGenre.setId(newGenreId);
            TreeSet<Genre> updateGenres = allGenres.getOrDefault(filmId,genres);
            updateGenres.add(newGenre);
            allGenres.put(filmId,updateGenres);
        }
        return allGenres;
    }

    private Map<Long, Set<Long>> getAllLikes() {
        Map<Long, Set<Long>> allLikes = new HashMap<>();
        SqlRowSet LikesRows = jdbcTemplate.queryForRowSet("SELECT FILM_ID, USER_ID FROM PUBLIC.FILMS_LIKES");
        while (LikesRows.next()) {
            Long newLikeId = LikesRows.getLong("USER_ID");
            Long filmId = LikesRows.getLong("FILM_ID");
            Set<Long> Likes = new HashSet<>();
            Set<Long> updateLikes = allLikes.getOrDefault(filmId,Likes);
            updateLikes.add(newLikeId);
            allLikes.put(filmId,updateLikes);
        }
        return allLikes;
    }


//    private Film mapRowToFilm(ResultSet rs, int rn) throws SQLException {
//        Mpa mpa = new Mpa();
//        mpa.setId(rs.getInt("RATING_ID"));
//        Film film = new Film();
//        film.setId(rs.getLong("FILM_ID"));
//        film.setName(rs.getString("FILM_NAME"));
//        film.setDescription(rs.getString("DESCRIPTION"));
//        film.setReleaseDate(rs.getDate("RELEASE_DATE").toLocalDate());
//        film.setDuration(rs.getInt("DURATION"));
//        film.setMpa(mpa);
//        return film;
//    }

    public void updateFilm(Film film) {
        String sqlQuery = "update FILM SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        saveGenres(film);
    }

    private void saveGenres(Film film) {
        final Long filmId = film.getId();
        jdbcTemplate.update("delete from FILM_GENRE where FILM_ID = ?", filmId);
        final Set<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }
        final ArrayList<Genre> genreList = new ArrayList<>(genres);
        jdbcTemplate.batchUpdate(
                "insert into FILM_GENRE (FILM_ID, GENRE_ID) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, genreList.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genreList.size();
                    }
                });
    }

    private void saveLikes(Film film) {
        final Long filmId = film.getId();
        jdbcTemplate.update("DELETE FROM PUBLIC.FILMS_LIKES WHERE FILM_ID = ?", filmId);
        final Set<Long> newLikedList = film.getLikesIds();
        if (newLikedList == null || newLikedList.isEmpty()) {
            return;
        }
        final ArrayList<Long> likesList = new ArrayList<>(newLikedList);
        jdbcTemplate.batchUpdate(
                "INSERT INTO FILMS_LIKES (FILM_ID, USER_ID) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, likesList.get(i));
                    }

                    public int getBatchSize() {
                        return likesList.size();
                    }
                });
//        for (Long newLike: newLikedList) {
//            jdbcTemplate.update("INSERT INTO PUBLIC.FILMS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)", item.getId(),newLike);

    }
}
