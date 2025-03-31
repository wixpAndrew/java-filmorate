package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.*;

import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public Object appendUser(@RequestBody User user) {
        try {
            checkingUser(user);
            if (users.containsKey(user.getId())) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            user.setId(generateId());
            users.put(user.getId(), user);
            log.info("ДОБАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации при добавлении пользователя: {}", exception.getMessage());
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (DuplicatedDataException ex) {
            log.error("Ошибка! дубликация при добавлении пользователя: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public Object updateUser(@RequestBody User user) {
        try {
            User finalUser = users.get(user.getId());
            checkingUser(user);

            boolean isNewEmail = !user.getEmail().equals(users.get(user.getId()).getEmail());

            boolean emailExists = users.values().stream()
                    .anyMatch(userByBase -> userByBase.getEmail().equals(user.getEmail()));

            if (finalUser == null) {
                throw new IllegalArgumentException("Пользователь не найден");
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

            if (user.getLogin() != null) {
                finalUser.setLogin(user.getLogin());
            }
            users.put(user.getId(), finalUser);
            log.info("ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (ValidationException exception) {
            log.error("Ошибка валидации при обновлении пользователя: {}", exception.getMessage());
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException exception) {
            log.error("Ошибка валидации при обновлении пользователя: {}", exception.getMessage());
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (DuplicatedDataException exception) {
            log.error("Ошибка валидации при обновлении пользователя: {}", exception.getMessage());
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private int generateId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return (int) ++currentMaxId;
    }

    private void checkingUser(User user) {
       if (!user.getEmail().contains("@") || user.getEmail() == null) {
           throw new ValidationException("ошибка в почте !");
       }

       if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("ошибка в логине !");
        }

       if (user.getBirthday().isAfter(LocalDateTime.now())) {
           throw new ValidationException("ошибка в указании Вашего Дня Рождения !");
       }
    }
}