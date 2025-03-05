package ru.yandex.practicum.filmorate.model;

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

    LocalDateTime releaseDate;

    Duration duration;
}
