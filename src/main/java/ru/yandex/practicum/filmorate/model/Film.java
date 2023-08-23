package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.FirstFilmDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.*;

@Data
//@Builder
public class Film extends Item {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @FirstFilmDate
    private Date releaseDate;
    @Positive
    private int duration;
    @JsonIgnore
    private Set<Long> likesIds = new HashSet<>();
    private Set<Genre> genres =  new TreeSet<>((Genre g1, Genre g2) -> g1.getId() - g2.getId());
    private Mpa mpa;

    public Film(String name, String description, Date releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(Long id, String name, String description, Date releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film() {
    }

    public void addLike(Long id) {
        likesIds.add(id);
    }

    public void deleteLike(Long id) {
        likesIds.remove(id);
    }

    public int getCountLike() {
        return likesIds.size();
    }

    public void setGenres(Set<Genre> genres) {
        this.genres.clear();
        for (Genre genre : genres) {
            if (!this.genres.contains(genre)) {
                this.genres.add(genre);
            }
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("FILM_NAME", name);
        values.put("DESCRIPTION", description);
        values.put("RELEASE_DATE", releaseDate);
        values.put("DURATION", duration);
        values.put("RATING_ID",mpa.getId());
        return values;
    }
}
