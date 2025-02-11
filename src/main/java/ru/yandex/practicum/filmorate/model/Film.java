package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class Film {
    Long id;
    String name;
    @Size(max = 200, message = "������������ ����� �������� � 200 ��������")
    String description;
    LocalDate releaseDate;
    @Positive(message = "����������������� ������ ������ ���� ������������� ������")
    int duration;
}