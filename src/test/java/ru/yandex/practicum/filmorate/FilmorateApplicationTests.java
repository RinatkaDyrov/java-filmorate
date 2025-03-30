package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FilmorateApplicationTests {

	@Autowired
	private UserService userService;

	@Autowired
	private FilmService filmService;

	@Test
	void contextLoads() {
		assertNotNull(userService);
		assertNotNull(filmService);
	}

}
