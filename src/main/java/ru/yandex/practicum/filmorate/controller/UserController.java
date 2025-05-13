package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

    @GetMapping
    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.getAll();
    }

    @PostMapping
    public ResponseEntity<User> appendUser(@RequestBody User user) {
        try {
            User created = inMemoryUserStorage.append(user);
            log.info("ДОБАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");
            return new ResponseEntity<>(created, HttpStatus.OK);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации при добавлении пользователя: {}", exception.getMessage());
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DuplicatedDataException ex) {
            log.error("Ошибка! дубликация при добавлении пользователя: {}", ex.getMessage());
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        try {
            User updated = inMemoryUserStorage.update(user);
            log.info("ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }catch (ValidationException exception) {
            log.error("Ошибка валидации при обновлении пользователя: {}", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException exception) {
            log.error("Ошибка валидации при обновлении пользователя: {}", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
