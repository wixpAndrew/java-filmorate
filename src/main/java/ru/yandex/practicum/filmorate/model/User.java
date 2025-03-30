package ru.yandex.practicum.filmorate.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    int id;

    String username;

    String login;

    String password;

    String email;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    LocalDateTime birthday;

    public User(int id, String username, String login, String password, String email, LocalDateTime birthday) {
        this.id = id;
        this.username = username;
        this.login = login;
        this.password = password;
        this.email = email;
        this.birthday = birthday;
    }
}
