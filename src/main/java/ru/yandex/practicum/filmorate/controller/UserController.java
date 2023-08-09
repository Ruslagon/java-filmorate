package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController

public class UserController {
    final private Set<User> users = new HashSet<>();
    private final Map<Integer, User> users2 = new HashMap<>();
    Logger log = (Logger) LoggerFactory.getLogger(UserController.class);
    private int id = 1;
    @GetMapping("/users")
    public List<User> findAll(){
        return new ArrayList<User>(users2.values());
    }

    @PostMapping("/users")
    public User create(@Valid @RequestBody User user) throws ValidationException {
        if (validation(user)) {
            log.warn("пользователь имеет неверные данные");
            throw new ValidationException("пользователь имеет неверные данные");
        }
        user.setId(id);
        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        users2.put(id,user);
        id++;
        log.info("добавлен пользователь : {}", user.toString());
        return user;
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) throws ValidationException {
        if (validation(user) || !users2.containsKey(user.getId())) {
            log.warn("пользователь имеет неверные данные или пользователя нет в библиотеке");
            throw new ValidationException("пользователь имеет неверные данные или пользователя нет в библиотеке");
        }
        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        users2.put(user.getId(),user);
        log.info("обновлен пользователь : {}", user.toString());
        return user;
    }

    public boolean validation(User user) {
        LocalDate now = LocalDate.now();
        return user.getLogin().contains(" ") || user.getBirthday().isAfter(now) || user.getLogin().isBlank() || user.getEmail().isBlank()
                || !user.getEmail().contains("@");
    }
}
