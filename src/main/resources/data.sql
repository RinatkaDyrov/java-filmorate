/*INSERT INTO rating_mpa (id, name)
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
INSERT INTO genre (name) VALUES ('Боевик');*/

INSERT INTO rating_mpa (NAME)
SELECT 'G'
WHERE NOT EXISTS(SELECT 1 From rating_mpa WHERE NAME = 'G');
INSERT INTO rating_mpa (NAME)
SELECT 'PG'
WHERE NOT EXISTS(SELECT 1 From rating_mpa WHERE NAME = 'PG');
INSERT INTO rating_mpa (NAME)
SELECT 'PG-13'
WHERE NOT EXISTS(SELECT 1 From rating_mpa WHERE NAME = 'PG-13');
INSERT INTO rating_mpa (NAME)
SELECT 'R'
WHERE NOT EXISTS(SELECT 1 From rating_mpa WHERE NAME = 'R');
INSERT INTO rating_mpa (NAME)
SELECT 'NC-17'
WHERE NOT EXISTS(SELECT 1 From rating_mpa WHERE NAME = 'NC-17');
INSERT INTO GENRE (NAME)
SELECT 'Комедия'
WHERE NOT EXISTS(SELECT 1 From GENRE WHERE NAME = 'Комедия');
INSERT INTO GENRE (NAME)
SELECT 'Драма'
WHERE NOT EXISTS(SELECT 1 From GENRE WHERE NAME = 'Драма');
INSERT INTO GENRE (NAME)
SELECT 'Мультфильм'
WHERE NOT EXISTS(SELECT 1 From GENRE WHERE NAME = 'Мультфильм');
INSERT INTO GENRE (NAME)
SELECT 'Триллер'
WHERE NOT EXISTS(SELECT 1 From GENRE WHERE NAME = 'Триллер');
INSERT INTO GENRE (NAME)
SELECT 'Документальный'
WHERE NOT EXISTS(SELECT 1 From GENRE WHERE NAME = 'Документальный');
INSERT INTO GENRE (NAME)
SELECT 'Боевик'
WHERE NOT EXISTS(SELECT 1 From GENRE WHERE NAME = 'Боевик');
