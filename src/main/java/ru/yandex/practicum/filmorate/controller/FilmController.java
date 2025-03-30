package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;

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
    public Object appendFilm(@RequestBody Film film) {
        try {
            checkingFilm(film);
            film.setId(generateId());
            films.put(film.getId(), film);
            log.info("ДОБАВЛЕНИЕ ФИЛЬМА");
            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (ValidationException ex) {
            log.error("Ошибка валидации при добавлении фильма: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public Object updateFilm(@RequestBody Film film) {
        try {
            checkingFilm(film);
            films.put(film.getId(), film);
            log.info("ОБНОВЛЕНИЕ ФИЛЬМА");
            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (ValidationException ex) {
            log.error("Ошибка валидации при обновлении фильма: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
            throw new ValidationException("Имя не может быть пустым!");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может превышать 200 символов!");
        }

        if (film.getLocalDateTime().isBefore(DATE_MIN)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года!");
        }

        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом!");
        }
    }
}