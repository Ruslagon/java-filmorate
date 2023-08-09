package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
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
    private HashMap<Long,FriendshipStatus> friends = new HashMap<>();
    @JsonIgnore
    private Set<Long> likedFilms = new HashSet<>();

    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }

    public void addFriends(Long friendId, FriendshipStatus status) {
        if (id.equals(friendId)) {
            throw new ValidationException("Нельзя добавлять в друзья самого себя");
        }
        friends.put(friendId, status);
    }

    public void deleteFriends(Long strangerId) {
        friends.remove(strangerId);
    }

    public FriendshipStatus getFriendshipStatus(Long otherUserId) {
        if (!friends.containsKey(otherUserId)) {
            return null;
        }
        return friends.get(otherUserId);
    }

    public void addLikedFilm(Long filmId) {
        likedFilms.add(filmId);
    }

    public void deleteLikedFilm(Long filmId) {
        likedFilms.remove(filmId);
    }
}
