package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User append(User user);

    User update(User user);

    Collection<User> getAllUsers();

    User getUserById(int id);
}
