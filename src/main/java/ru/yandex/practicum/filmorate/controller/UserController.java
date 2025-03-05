package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;


@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User appendUser(@RequestBody User user) {
        if (user.getEmail() == null ) { // имеется ли почта
            throw new DuplicatedDataException("Имейл должен быть указан");
        }
        if (users.containsKey(user.getId())) { // был ли добавлен раньше
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        user.setId(generateId());
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        User finalUser = users.get(user.getId());

        boolean isNewEmail = !user.getEmail().equals(users.get(user.getId()).getEmail());

        boolean emailExists = users.values().stream()
                .anyMatch(userByBase -> userByBase.getEmail().equals(user.getEmail()));

        if (finalUser == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        if (Integer.valueOf(user.getId()) == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (isNewEmail && emailExists) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        } else {
            finalUser.setEmail(user.getEmail());
        }

        if (user.getPassword() != null) {
            finalUser.setPassword(user.getPassword());
        }   

        if (user.getUsername() != null) {
            finalUser.setUsername(user.getUsername());
        }
        users.put(user.getId(), finalUser);
        return finalUser;
    }

    private int generateId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return (int) ++currentMaxId;
    }
}
