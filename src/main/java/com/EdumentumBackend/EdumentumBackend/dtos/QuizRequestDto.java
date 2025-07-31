package com.EdumentumBackend.EdumentumBackend.dtos;

import com.EdumentumBackend.EdumentumBackend.commom.model.QuestionData;
import com.EdumentumBackend.EdumentumBackend.entity.QuizCreationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class QuizRequestDto {
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    private boolean visibility;

    @Min(value = 0, message = "Total must be non-negative")
    private int total;

    @NotBlank(message = "Topic cannot be blank")
    private String topic;

    @NotNull(message = "Quiz creation type is required")
    @Enumerated(EnumType.STRING)
    private QuizCreationType quizCreationType;

    @NotEmpty(message = "QuestionData must not be empty")
    private List<QuestionData> questions;
}
