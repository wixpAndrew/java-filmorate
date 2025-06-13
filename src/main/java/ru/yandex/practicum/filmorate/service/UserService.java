package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
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

    public List<User> getCommonFriends(int userId, int otherUserId) {
        List<User> userFriends = friends.getOrDefault(userId, new ArrayList<>());
        List<User> otherUserFriends = friends.getOrDefault(otherUserId, new ArrayList<>());

        // Фильтруем и находим общих друзей
        List<User> common = new ArrayList<>();
        for (User friend : userFriends) {
            if (otherUserFriends.contains(friend)) {
                common.add(friend);
            }
        }

        return common;
    }

    public List<User> getFriends(int userId) {
        return friends.getOrDefault(userId, new ArrayList<>());
    }

    private User getUser(int id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден: id=" + id);
        }
        return user;
    }
}