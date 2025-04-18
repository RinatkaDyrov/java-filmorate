package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;


    public Collection<DirectorDto> getAllDirectors() {
        log.debug("Запрос списка всех режиссеров в сервисе");
        return directorStorage.findAll()
                .stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    public DirectorDto getDirectorById(long id) {
        log.debug("Запрос режиссера (ID: {}) в сервисе", id);
        return DirectorMapper.mapToDirectorDto(directorStorage.findById(id));
    }

    public DirectorDto createDirector(NewDirectorRequest request) {
        log.debug("Создание нового режиссера в сервисе");
        if (!request.hasValidName()) {
            throw new IllegalArgumentException("Имя режиссера не может быть пустым.");
        }

        Director director = DirectorMapper.mapToDirector(request);
        director = directorStorage.create(director);
        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto updateDirector(UpdateDirectorRequest request) {
        log.debug("Обновление режиссера в сервисе");
        Director updDirector = directorStorage.findById(request.getId());
        if (updDirector == null) {
            log.warn("Режиссер (ID: {}) не найден. ООбновление прервано", request.getId());
            throw new NotFoundException("Режиссер с таким id не найден");
        }
        updDirector = DirectorMapper.updateDirectorFields(updDirector, request);
        updDirector = directorStorage.update(updDirector);
        return DirectorMapper.mapToDirectorDto(updDirector);
    }

    public void deleteDirectorById(long id) {
        log.debug("Удаление режиссера в сервисе");
        Director delDirector = directorStorage.findById(id);
        if (delDirector == null) {
            log.warn("Режиссер (ID: {}) не найден. Удаление прервано", id);
            throw new NotFoundException("Режиссер с таким id не найден");
        }
        directorStorage.delete(id);
    }
}
