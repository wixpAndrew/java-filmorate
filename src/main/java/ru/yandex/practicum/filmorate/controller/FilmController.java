package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;


@RestController
@RequestMapping("/films")
public class FilmController {

    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    private InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    private InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    private FilmService filmService = new FilmService(inMemoryFilmStorage, inMemoryUserStorage);

    @GetMapping
    public Collection<Film> getFilms() {
        return inMemoryFilmStorage.getAll();
    }

    @PostMapping
    public ResponseEntity<Film> appendFilm(@RequestBody Film film) {
        try {
            Film finalFilm = inMemoryFilmStorage.append(film);
            log.info("ДОБАВЛЕНИЕ ФИЛЬМА");
            return new ResponseEntity<>(finalFilm, HttpStatus.OK);
        } catch (ValidationException ex) {
            log.error("Ошибка валидации при добавлении фильма: {}", ex.getMessage());
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        try {
            Film finalFilm = inMemoryFilmStorage.update(film);
            log.info("ОБНОВЛЕНИЕ ФИЛЬМА");
            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (ValidationException ex) {
            log.error("Ошибка валидации при обновлении фильма: {}", ex.getMessage());
            return new ResponseEntity<>(film, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException exception) {
            log.error("Ошибка валидации при обновлении фильма: {}", exception.getMessage());
            return new ResponseEntity<>(film, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //-------------------------------------------------------------------------------------
    //--------------------------ЛАЙКИ-------------------------------------

    @PutMapping("/{filmId}/like/{userId}")
    public  ResponseEntity<Film> likingByUser(@PathVariable int filmId, @PathVariable int userId) {
        try {
            Film film = inMemoryFilmStorage.getById(filmId);

            filmService.addLike(filmId, userId);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (NotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Film> deleteLiking(@PathVariable int filmId, @PathVariable int userId) {
        try {
            Film film = inMemoryFilmStorage.getById(filmId);
            filmService.removeLike(filmId, userId);

            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (NotFoundException exception) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}