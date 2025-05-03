package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int count = 0;


    @Override
    public User append(User user) {
        validateUser(user);
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(++count);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        validateUser(user);
        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        boolean isNewEmail = !existingUser.getEmail().equals(user.getEmail());
        boolean emailExists = users.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && u.getId() != user.getId());

        if (isNewEmail && emailExists) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        existingUser.setLogin(user.getLogin());
        existingUser.setName(user.getName());
        existingUser.setPassword(user.getPassword());
        existingUser.setEmail(user.getEmail());
        existingUser.setBirthday(user.getBirthday());

        return existingUser;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getById(int id) {
        return users.get(id);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Ошибка в почте!");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Ошибка в логине!");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Ошибка в указании дня рождения!");
        }
    }
}
