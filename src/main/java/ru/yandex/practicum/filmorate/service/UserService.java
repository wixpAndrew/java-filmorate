package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.awt.im.spi.InputMethod;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final HashMap<Integer, List<User>> friends = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        User friend = getUser(friendId); //получаем друга как user по айди
        User user = getUser(userId); // проверка есть ли вообще
        friends.computeIfAbsent(userId, k -> new ArrayList<>()).add(friend); // добавляем в мапу айди юзера, а потом список друзей
    }

    public void removeFriend(int userId, int friendId) {
        List<User> userFriends = friends.get(userId);
        if (userFriends != null) {
            userFriends.removeIf(u -> u.getId() == friendId);
        }
    }

    public List<User> getFriends(int userId) {
        return friends.getOrDefault(userId, new ArrayList<>());
    }

    private User getUser(int id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден: id=" + id);
        }
        return user;
    }
}