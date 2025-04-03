package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.genre.GenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Component
@Slf4j
public class GenreStorage {

    private final GenreRepository genreRepository;

    public GenreStorage(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Collection<Genre> findAll() {
        return genreRepository.findAllGenres();
    }

    public Genre findById(long id) {
        log.info("Ищем жанр в хранилище по id {}", id);
        return genreRepository.findGenreById(id);

    }
}
