package ru.yandex.practicum.filmorate.dto.review;

import lombok.Data;

@Data
public class ReviewDto {
    private Long reviewId;
    String content;
    private Boolean isPositive;
    private Long userId;
    Long filmId;
    private int useful;
}
