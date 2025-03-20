package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Film.
 */
@Data
public class Film {
    int id;

    String name;

    String description;

    LocalDateTime localDateTime;

    Duration duration;


    public Film(int id, String name, String description, LocalDateTime localDateTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.localDateTime = localDateTime;
        this.duration = duration;
    }
}
