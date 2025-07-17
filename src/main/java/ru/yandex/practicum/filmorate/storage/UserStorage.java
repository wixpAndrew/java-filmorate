package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User append(User user);

    User update(User user);

    Collection<User> getAllUsers();

    User getUserById(int id);
}
