package ru.yandex.practicum.filmorate.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    int id;

    String name;

    String login;

    String password;

    String email;

    LocalDate birthday;

    public User(int id, String username, String login, String password, String email, LocalDate localDate) {
        this.id = id;
        this.name = username;
        this.login = login;
        this.password = password;
        this.email = email;
        this.birthday = localDate;
    }


    public User() {

    }
}
