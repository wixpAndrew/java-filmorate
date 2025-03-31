package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class Film {
    int id;

    String name;

    String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
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
