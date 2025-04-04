package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Post {
    int id;
    String description;
    Instant postDate;
}
