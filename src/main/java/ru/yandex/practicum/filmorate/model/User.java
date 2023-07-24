package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User extends Item{
    private Long id;
    @Email
    private String email;
    private String name;
    @NotNull
    @NotBlank
    private String login;
    private LocalDate birthday;
    private Set<Long> friendsIds;
    private Set<Long> likedFilms;

    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

    public void addFriends(Long friendId) {
        if (friendsIds == null) {
            friendsIds = new HashSet<>();
        }
        if (id.equals(friendId)) {
            throw new ValidationException("Нельзя добавлять в друзья самого себя");
        }
        friendsIds.add(friendId);
    }

    public void deleteFriends(Long strangerId) {
        if (friendsIds == null) {
            friendsIds = new HashSet<>();
        }
        friendsIds.remove(strangerId);
    }

    public void addLikedFilm(Long filmId) {
        if (likedFilms == null) {
            likedFilms = new HashSet<>();
        }
        likedFilms.add(filmId);
    }

    public void deleteLikedFilm(Long filmId) {
        if (likedFilms == null) {
            likedFilms = new HashSet<>();
        }
        likedFilms.remove(filmId);
    }
}
