package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Level;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
@RestController

public class UserController {
    final private Set<User> users = new HashSet<>();
    Logger log = (Logger) LoggerFactory.getLogger(UserController.class);
    private int id = 1;
    @GetMapping("/users")
    public Set<User> findAll(){
        return users;
    }

    @PostMapping("/users")
    public User create(@Valid @RequestBody User user) throws ValidationException {
        if (validation(user)) {
            log.setLevel(Level.WARN);
            log.warn("пользователь имеет неверные данные");
            throw new ValidationException("пользователь имеет неверные данные");
        }
        user.setId(id);
        id++;
        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        users.add(user);
        log.setLevel(Level.INFO);
        log.info("добавлен пользователь : {}", user.toString());
        return user;
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) throws ValidationException {
        if (validation(user) || !users.contains(user)) {
            log.setLevel(Level.WARN);
            log.warn("пользователь имеет неверные данные или пользователя нет в библиотеке");
            throw new ValidationException("пользователь имеет неверные данные или пользователя нет в библиотеке");
        }
        users.remove(user);
        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        users.add(user);
        log.setLevel(Level.INFO);
        log.info("обновлен пользователь : {}", user.toString());
        return user;
    }

    private boolean validation(User user) {
        LocalDate now = LocalDate.now();
        return user.getLogin().contains(" ") || user.getBirthday().isAfter(now);
    }
}
