package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.validator.MyPastOrPresent;

import javax.validation.constraints.*;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Data
//@Builder
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
    @MyPastOrPresent
    private Date birthday;
    @JsonIgnore
    private HashMap<Long, FriendShipStatus> friends = new HashMap<>();
//    @JsonIgnore
//    private Set<Long> likedFilms = new HashSet<>();

    public User(String email, String login, Date birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }
    public User() {}

    public void addFriends(Long friendId, FriendShipStatus status) {
        if (id.equals(friendId)) {
            throw new ValidationException("Нельзя добавлять в друзья самого себя");
        }
        friends.put(friendId, status);
    }

    public void deleteFriends(Long strangerId) {
        friends.remove(strangerId);
    }

    public FriendShipStatus getFriendshipStatus(Long otherUserId) {
        if (!friends.containsKey(otherUserId)) {
            return null;
        }
        return friends.get(otherUserId);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("EMAIL", email);
        values.put("LOGIN", login);
        values.put("USER_NAME", name);
        values.put("BIRTHDAY", birthday);

        return values;
    }

//    public void addLikedFilm(Long filmId) {
//        likedFilms.add(filmId);
//    }

//    public void deleteLikedFilm(Long filmId) {
//        likedFilms.remove(filmId);
//    }
}
