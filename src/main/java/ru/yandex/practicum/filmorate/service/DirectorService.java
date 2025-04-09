package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;


    public Collection<DirectorDto> getAllDirectors() {
        return directorStorage.findAll()
                .stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    public DirectorDto getDirectorById(long id) {
        return DirectorMapper.mapToDirectorDto(directorStorage.findById(id));
    }

    public DirectorDto createDirector(NewDirectorRequest request) {
        Director director = DirectorMapper.mapToDirector(request);
        director = directorStorage.create(director);

    }

    public DirectorDto updateDirector(UpdateDirectorRequest request) {
    }

    public void deleteDirectorById(long id) {
    }
}
