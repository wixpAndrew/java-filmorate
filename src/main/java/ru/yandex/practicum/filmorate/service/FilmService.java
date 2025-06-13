package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
public class FilmService {

    private final FilmStorage FilmStorage;
    private final UserStorage UserStorage;

    public FilmService(FilmStorage FilmStorage, UserStorage UserStorage) {
        this.FilmStorage = FilmStorage;
        this.UserStorage = UserStorage;
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        if (UserStorage.getUserById(userId) == null) { // есть ли вообще такой User
            throw new NotFoundException("Пользователь не найден: id=" + userId);
        }
        if (FilmStorage.getById(filmId).getLikesAndUsersId().contains(userId)) { // user уже лайкнул
            throw new DuplicatedDataException("Пользователь уже поставил лайк!");
        }
        film.getLikesAndUsersId().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        if (FilmStorage.getById(filmId) == null) {
            throw new NotFoundException("Такого фильма нет! filmid : " + filmId);
        }
        Film film = getFilm(filmId);
        if (!film.getLikesAndUsersId().contains(userId)) {
            throw new NotFoundException("У пользователя нет лайка для этого фильма: userId=" + userId);
        }
        film.getLikesAndUsersId().remove(userId);
    }

//    public List<Film> getTopFilms(int count) {
//        return inMemoryFilmStorage.getAll().stream()
//                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
//                .limit(count)
//                .collect(Collectors.toList());
//    }x

    private Film getFilm(int id) {
        Film film = FilmStorage.getById(id);
        if (film == null) {
            throw new NotFoundException("Фильм не найден: id=" + id);
        }
        return film;
    }
}