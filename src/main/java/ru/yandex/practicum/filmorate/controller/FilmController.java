package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import ru.yandex.practicum.filmorate.exception.ValidationException;

import static java.rmi.server.LogStream.log;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private HashMap<Integer, Film> films = new HashMap<>();

    private final static LocalDateTime DATE_MIN = LocalDateTime.of(1895, Month.DECEMBER, 28, 0, 0);

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film appendFilm(@RequestBody Film film) {
        checkingFilm(film);
        films.put(film.getId(), film);
        log.info("ДОБАВЛЕНИЕ ФИЛЬМА");
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        checkingFilm(film);
        films.put(film.getId(), film);
        log.info("ОБНОВЛЕНИЕ ФИЛЬМА");
        return film;
    }

    private int generateId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return (int) ++currentMaxId;
    }

    private void checkingFilm(Film film) {
        if (film.getName().isEmpty()) {
            throw new ValidationException("имя не может быть пустым!");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("описание не может превышать 200 символов!");
        }

        if (film.getLocalDateTime().isBefore(DATE_MIN)) {
            throw new ValidationException("дата релиза не может быть раньше 28 декабря 1895 года!");
        }

        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом!");
        }
    }
}