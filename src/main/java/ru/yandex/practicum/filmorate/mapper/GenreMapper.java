package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class GenreMapper {
    public static GenreDto mapToGenreDto(Genre genre) {
        log.info("Конвертируем Genre в GenreDto");
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

}
