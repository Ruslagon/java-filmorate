package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class Film {
    int id;
    @EqualsAndHashCode.Exclude
    @NotNull
    @NotBlank
    String name;
    @EqualsAndHashCode.Exclude
    String description;
    @EqualsAndHashCode.Exclude
    LocalDate releaseDate;
    @EqualsAndHashCode.Exclude
    int duration;
}
