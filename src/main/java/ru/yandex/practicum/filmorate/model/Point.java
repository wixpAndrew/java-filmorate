package ru.yandex.practicum.filmorate.model;

import lombok.*;

@ToString
@Value
@Builder(toBuilder = true)
public class Point {
    int x;
    int y;
    String description;
}