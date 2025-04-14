package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    private Long reviewId;

    @NotBlank(message = "Отзыв должен быть заполнен")
    String content;

    @NotNull(message = "Нужно указать тип отзыва")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя должен быть указан")
    private Long userId;

    @NotNull(message = "ID фильма должен быть указан")
    Long filmId;

    private int useful;

    public Review(Long reviewId, String content, Boolean isPositive, Long userId, Long filmId, int useful) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }
}
