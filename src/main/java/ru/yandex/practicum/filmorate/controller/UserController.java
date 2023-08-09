package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")

public class UserController {
    private Logger log = (Logger) LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("обновлен пользователь : {}", user.toString());
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User findOne(@PathVariable Long id) {
        if (idValidation(id)) {
            throw new NotFoundException("id пользователя введено неверно - " + id);
        }
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Map<Long,Map<Long, FriendshipStatus>> addToFriends(@PathVariable(required = false) Long id,
                                                              @PathVariable(required = false) Long friendId) {
        if (idValidation(id)) {
            throw new NotFoundException("id пользователя введено неверно - " + id);
        }
        if (idValidation(friendId)) {
            throw new NotFoundException("id пользователя введено неверно - " + friendId);
        }
        return userService.addFriend(id,friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Map<Long,Map<Long, FriendshipStatus>> deleteFromFriends(@PathVariable Long id,
                                                  @PathVariable Long friendId) {
        if (idValidation(id)) {
            throw new NotFoundException("id пользователя введено неверно - " + id);
        }
        if (idValidation(friendId)) {
            throw new NotFoundException("id пользователя введено неверно - " + friendId);
        }
        return userService.deleteFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        if (idValidation(id)) {
            throw new NotFoundException("id пользователя введено неверно - " + id);
        }
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id,
                                       @PathVariable Long otherId) {
        if (idValidation(id)) {
            throw new NotFoundException("id пользователя введено неверно - " + id);
        }
        if (idValidation(otherId)) {
            throw new NotFoundException("id пользователя введено неверно - " + otherId);
        }
        return userService.getMutualFriends(id,otherId);
    }

    public boolean idValidation(Long id) {
        return (id == null || id <= 0);
    }
}
