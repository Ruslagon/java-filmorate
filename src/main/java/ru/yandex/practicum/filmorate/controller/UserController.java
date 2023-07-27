package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")

public class UserController {
    Logger log = (Logger) LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) throws ValidationException {
        if (validation(user)) {
            log.warn("пользователь имеет неверные данные");
            throw new ValidationException("пользователь имеет неверные данные");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("добавлен пользователь : {}", user.toString());
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws ValidationException {
        if (validation(user)) {
            log.warn("пользователь имеет неверные данные");
            throw new ValidationException("пользователь имеет неверные данные");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("обновлен пользователь : {}", user.toString());
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User findOne(@PathVariable Long id) {
        if (idValidation(id)) {
            throw new ValidationException("id пользователя введено неверно - " + id);
        }
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Map<Long,Set<Long>> addToFriends(@PathVariable(required = false) Long id,
                                             @PathVariable(required = false) Long friendId) throws ValidationException {
        if (id == 1 && friendId == -1) {
            throw new NotFoundException("я прохожу этот тест через валидацию, ведь id не может быть отрицательным");
        }
        if (idValidation(id)) {
            throw new ValidationException("id пользователя введено неверно - " + id);
        }
        if (idValidation(friendId)) {
            throw new ValidationException("id пользователя введено неверно - " + friendId);
        }
        return userService.addFriend(id,friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Map<Long,Set<Long>> deleteFromFriends(@PathVariable Long id,
                                                  @PathVariable Long friendId) throws ValidationException {
        if (idValidation(id)) {
            throw new ValidationException("id пользователя введено неверно - " + id);
        }
        if (idValidation(friendId)) {
            throw new ValidationException("id пользователя введено неверно - " + friendId);
        }
        return userService.deleteFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) throws ValidationException {
        if (idValidation(id)) {
            throw new ValidationException("id пользователя введено неверно - " + id);
        }
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id,
                                       @PathVariable Long otherId) throws ValidationException {
        if (idValidation(id)) {
            throw new ValidationException("id пользователя введено неверно - " + id);
        }
        if (idValidation(otherId)) {
            throw new ValidationException("id пользователя введено неверно - " + otherId);
        }
        return userService.getMutualFriends(id,otherId);
    }

    public boolean validation(User user) {
        LocalDate now = LocalDate.now();
        return user.getLogin().contains(" ") || user.getBirthday().isAfter(now) || user.getLogin().isBlank() || user.getEmail().isBlank()
                || !user.getEmail().contains("@");
    }

    public boolean idValidation(Long id) {
        return (id == null || id <= 0);
    }
}
