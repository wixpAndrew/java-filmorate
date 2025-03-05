package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.Instant;

@Data
public class Post {
    int id;
    String description;
    Instant postDate;
}
