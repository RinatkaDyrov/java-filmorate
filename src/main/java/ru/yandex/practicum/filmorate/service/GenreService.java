package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<GenreDto> getAllGenres() {
        return genreStorage.findAll()
                .stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto findByGenreId(long id) {
        log.info("Ищем жанр в сервисе по id {}", id);
        return GenreMapper.mapToGenreDto(genreStorage.findById(id));
    }
}
