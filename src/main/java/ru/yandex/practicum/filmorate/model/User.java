package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User extends Item {
    private Long id;
    @Email
    @NotEmpty
    private String email;
    private String name;
    @NotBlank
    @Pattern(regexp = "\\S+", message = "логин не должен содержать пробелов")
    private String login;
    @NotNull
    @PastOrPresent
    private LocalDate birthday;
    @JsonIgnore
    private Set<Long> friendsIds = new HashSet<>();
    @JsonIgnore
    private Set<Long> likedFilms = new HashSet<>();

    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

    public void addFriends(Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("Нельзя добавлять в друзья самого себя");
        }
        friendsIds.add(friendId);
    }

    public void deleteFriends(Long strangerId) {
        friendsIds.remove(strangerId);
    }

    public void addLikedFilm(Long filmId) {
        likedFilms.add(filmId);
    }

    public void deleteLikedFilm(Long filmId) {
        likedFilms.remove(filmId);
    }
}
