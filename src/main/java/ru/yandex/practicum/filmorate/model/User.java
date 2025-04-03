package ru.yandex.practicum.filmorate.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User {
    int id;

    String username;

    String login;

    String password;

    String email;

    LocalDate birthday;

    public User(int id, String username, String login, String password, String email, LocalDate localDate) {
        this.id = id;
        this.username = username;
        this.login = login;
        this.password = password;
        this.email = email;
        this.birthday = localDate;
    }
}
