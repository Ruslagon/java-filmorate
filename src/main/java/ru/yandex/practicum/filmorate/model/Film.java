package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.FirstFilmDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film extends Item {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @FirstFilmDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    @JsonIgnore
    private Set<Long> likesIds = new HashSet<>();

    private Set<Genre> genres = new HashSet<>();

    private Rating rating;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, int duration) {
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
}
