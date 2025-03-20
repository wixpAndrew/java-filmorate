package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    int id;

    String username;

    String login;

    String password;

    String email;

    LocalDateTime birthday;
}
