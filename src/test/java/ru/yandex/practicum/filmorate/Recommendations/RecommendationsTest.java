package ru.yandex.practicum.filmorate.Recommendations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.like.LikeRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class RecommendationsTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private JdbcTemplate jdbc;


    @Test
    public void testFindRecommendedFilmIds() {
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM films");
        jdbc.update("DELETE FROM users");

        jdbc.update("INSERT INTO users(id, email, login, name, birthday) VALUES (1, 'a@mail.ru', 'user1', 'User 1', '1990-01-01')");
        jdbc.update("INSERT INTO users(id, email, login, name, birthday) VALUES (2, 'b@mail.ru', 'user2', 'User 2', '1991-01-01')");

        jdbc.update("INSERT INTO films(id, name, description, release_date, duration, rating_id) VALUES (1, 'Film 1', 'Desc', '2000-01-01', 120, 1)");
        jdbc.update("INSERT INTO films(id, name, description, release_date, duration, rating_id) VALUES (2, 'Film 2', 'Desc', '2000-01-01', 100, 1)");
        jdbc.update("INSERT INTO films(id, name, description, release_date, duration, rating_id) VALUES (3, 'Film 3', 'Desc', '2000-01-01', 90, 1)");

        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES (1, 1)");
        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES (2, 1)");
        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES (2, 2)");

        List<Long> recommendations = likeRepository.findRecommendedFilmIds(1L);

        assertThat(recommendations).containsExactly(2L);
    }

    @Test
    public void testNoRecommendationsWhenNoCommonLikes() {
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM films");
        jdbc.update("DELETE FROM users");

        jdbc.update("INSERT INTO users(id, email, login, name, birthday) VALUES (1, 'a@mail.ru', 'user1', 'User 1', '1990-01-01')");
        jdbc.update("INSERT INTO users(id, email, login, name, birthday) VALUES (2, 'b@mail.ru', 'user2', 'User 2', '1991-01-01')");

        jdbc.update("INSERT INTO films(id, name, description, release_date, duration, rating_id) VALUES (1, 'Film 1', 'Desc', '2000-01-01', 120, 1)");
        jdbc.update("INSERT INTO films(id, name, description, release_date, duration, rating_id) VALUES (2, 'Film 2', 'Desc', '2000-01-01', 100, 1)");

        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES (1, 1)");
        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES (2, 2)");

        List<Long> recommendations = likeRepository.findRecommendedFilmIds(1L);

        assertThat(recommendations).isEmpty();
    }

    @Test
    public void testNoRecommendationsWhenAlreadyLikedAll() {
        // Очистка
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM films");
        jdbc.update("DELETE FROM users");

        jdbc.update("INSERT INTO users(id, email, login, name, birthday) VALUES (1, 'a@mail.ru', 'user1', 'User 1', '1990-01-01')");
        jdbc.update("INSERT INTO users(id, email, login, name, birthday) VALUES (2, 'b@mail.ru', 'user2', 'User 2', '1991-01-01')");

        jdbc.update("INSERT INTO films(id, name, description, release_date, duration, rating_id) VALUES (1, 'Film 1', 'Desc', '2000-01-01', 120, 1)");
        jdbc.update("INSERT INTO films(id, name, description, release_date, duration, rating_id) VALUES (2, 'Film 2', 'Desc', '2000-01-01', 100, 1)");

        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES (1, 1)");
        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES (1, 2)");
        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES (2, 1)");
        jdbc.update("INSERT INTO likes(user_id, film_id) VALUES (2, 2)");

        List<Long> recommendations = likeRepository.findRecommendedFilmIds(1L);

        assertThat(recommendations).isEmpty();
    }

}
