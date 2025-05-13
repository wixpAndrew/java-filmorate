package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component("inMemory")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int count = 0;

    private final LocalDate dateMin = LocalDate.of(1895, 12, 28);

    @Override
    public Film append(Film film) {
        validateFilm(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            throw new IllegalArgumentException("такого фильма нет !");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    private int generateId() {
        return ++count;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым!");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может превышать 200 символов!");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(dateMin)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года!");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом!");
        }
    }

    public Film getById(int id) {
        return films.get(id);
    }
}