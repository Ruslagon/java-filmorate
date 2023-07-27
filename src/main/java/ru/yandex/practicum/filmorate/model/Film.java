package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film extends Item {
    Long id;
    @NotNull
    @NotBlank
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    private Set<Long> likesIds;

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
        if (likesIds == null) {
            likesIds = new HashSet<>();
        }
        likesIds.add(id);
    }

    public void deleteLike(Long id) {
        if (likesIds == null) {
            likesIds = new HashSet<>();
        }
        likesIds.remove(id);
    }
}
