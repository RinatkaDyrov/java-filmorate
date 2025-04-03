package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.genre.NewGenreRequest;
import ru.yandex.practicum.filmorate.model.Genre;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class GenreMapper {
    public static GenreDto mapToGenreDto(Genre genre) {
        log.info("Конвертируем Mpa в MpaDto");
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public static Genre mapToGenre(NewGenreRequest request) {
        Genre genre = new Genre();
        genre.setId(request.getId());
        genre.setName(request.getName());
        return genre;
    }
}
