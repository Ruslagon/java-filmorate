package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @EqualsAndHashCode.Exclude
    @Email
    private String email;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    @NotNull
    @NotBlank
    private String login;
    @EqualsAndHashCode.Exclude
    private LocalDate birthday;
}
