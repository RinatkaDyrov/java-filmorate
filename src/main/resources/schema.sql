create TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(100) NOT NULL,
    name VARCHAR(255),
    birthday DATE
);

create TABLE IF NOT EXISTS rating_mba (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL
);

create TABLE IF NOT EXISTS genre (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50)
);

create TABLE IF NOT EXISTS films (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    genre_id INT,
    rating_id INT,
    FOREIGN KEY (genre_id) REFERENCES genre(id),
    FOREIGN KEY (rating_id) REFERENCES rating_mba(id)
);

create TABLE IF NOT EXISTS likes (
    user_id BIGINT,
    film_id BIGINT,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE
);


create TABLE IF NOT EXISTS friendship (
    user_id BIGINT,
    friend_id BIGINT,
    confirm_status BOOLEAN,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
);