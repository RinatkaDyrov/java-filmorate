package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReviewRequest {

    @NotNull
    Long reviewId;

    @NotBlank(message = "Отзыв должен быть заполнен")
    String content;

    @NotNull(message = "Нужно указать тип отзыва")
    private Boolean isPositive;

    @NotNull(message = "ID пользователя должен быть указан")
    private Long userId;

    @NotNull(message = "ID фильма должен быть указан")
    Long filmId;

    private Integer useful;

    public boolean hasContent() {
        return !(content.isEmpty() || content.isBlank());
    }

    public boolean hasIsPositive() {
        return !(isPositive == null);
    }

    public boolean hasUserId() {
        return !(userId == null);
    }

    public boolean hasFilmId() {
        return !(filmId == null);
    }

    public boolean hasUseful() {
        return !(useful == null);
    }
}
