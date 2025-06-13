package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private UserService userService = new UserService(inMemoryUserStorage);


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
        } catch (ValidationException exception) {
            log.error("Ошибка валидации при обновлении пользователя: {}", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException exception) {
            log.error("Ошибка валидации при обновлении пользователя: {}", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //----------------------------------------------------------------------------------------
//----------------------- ДРУЗЬЯ--------------------------------------
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable int id, @PathVariable int friendId) {
        try {
            userService.addFriend(id, friendId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable int id) {
        try {
            List<User> result = userService.getFriends(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<List<User>> deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        try {
            userService.removeFriend(id, friendId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable int id , @PathVariable int otherId) {
        try {
            List<User> result = userService.getCommonFriends(id, otherId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
