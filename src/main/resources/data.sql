INSERT INTO rating_mpa (id, name)
            VALUES (1, 'G');
INSERT INTO rating_mpa (id, name)
            VALUES (2, 'PG');
INSERT INTO rating_mpa (id, name)
            VALUES (3, 'PG-13');
INSERT INTO rating_mpa (id, name)
            VALUES (4, 'R');
INSERT INTO rating_mpa (id, name)
            VALUES (5, 'NC-17');

INSERT INTO genre (name) VALUES ('Комедия');
INSERT INTO genre (name) VALUES ('Драма');
INSERT INTO genre (name) VALUES ('Мультфильм');
INSERT INTO genre (name) VALUES ('Триллер');
INSERT INTO genre (name) VALUES ('Документальный');
INSERT INTO genre (name) VALUES ('Боевик');

INSERT INTO films (name, description, release_date, duration, genre_id, rating_id)
VALUES ( 'Форест Гамп', 'description1', '2000-01-01', 120, 1, 3 );
INSERT INTO films (name, description, release_date, duration, genre_id, rating_id)
VALUES ( 'Отступники', 'description2', '2000-01-02', 120, 4, 3 );
INSERT INTO films (name, description, release_date, duration, genre_id, rating_id)
VALUES ( 'Титаник', 'description3', '2000-01-03', 120, 2, 3 );
INSERT INTO films (name, description, release_date, duration, genre_id, rating_id)
VALUES ( 'Формула один', 'description4', '2000-01-04', 120, 5, 3 );

