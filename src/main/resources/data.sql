-- Добавляем пользователей
INSERT INTO users (email, login, name, birthday) VALUES
('user1@example.com', 'user1', 'Иван Иванов', '1990-05-14'),
('user2@example.com', 'user2', 'Петр Петров', '1985-08-23'),
('user3@example.com', 'user3', 'Сергей Сергеев', '1995-02-10');

-- Добавляем рейтинги (MPA)
INSERT INTO rating_mba (name) VALUES
('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

-- Добавляем жанры
INSERT INTO genre (name) VALUES
('Комедия'), ('Драма'), ('Боевик'), ('Фантастика'), ('Ужасы');

-- Добавляем фильмы
INSERT INTO films (name, description, release_date, duration, genre_id, rating_id) VALUES
('Фильм 1', 'Описание фильма 1', '2000-07-20', 120, 1, 3),
('Фильм 2', 'Описание фильма 2', '2010-12-05', 95, 2, 2),
('Фильм 3', 'Описание фильма 3', '2015-03-18', 110, 3, 4);

-- Добавляем лайки
INSERT INTO likes (user_id, film_id) VALUES
(1, 1), (2, 1), (3, 2), (1, 3), (2, 3);

-- Добавляем дружбу (user_id отправляет запрос в друзья friend_id)
INSERT INTO friendship (user_id, friend_id, confirm_status) VALUES
(1, 2, TRUE),  -- Иван и Петр друзья
(1, 3, FALSE), -- Иван отправил заявку Сергею, но не подтверждена
(2, 3, TRUE);  -- Петр и Сергей друзья
