package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<DirectorDto> findAll() {
        log.debug("Запрос списка режиссеров");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public DirectorDto findDirectorById(@PathVariable long id) {
        log.debug("Запрос режиссера по ID: {}", id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto create(@Valid @RequestBody NewDirectorRequest request) {
        log.debug("Добавление нового режиссера");
        return directorService.createDirector(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public DirectorDto update(@Valid @RequestBody UpdateDirectorRequest request) {
        log.debug("Обновление режиссера ID: {}", request.getId());
        return directorService.updateDirector(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDirector(@PathVariable long id) {
        log.debug("Удаление режиссера ID: {}", id);
        directorService.deleteDirectorById(id);
    }
}