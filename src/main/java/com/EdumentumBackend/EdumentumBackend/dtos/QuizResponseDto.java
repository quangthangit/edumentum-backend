package com.EdumentumBackend.EdumentumBackend.dtos;

import com.EdumentumBackend.EdumentumBackend.commom.model.QuestionData;
import com.EdumentumBackend.EdumentumBackend.entity.QuizCreationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class QuizResponseDto {
    private Long quizId;
    private String title;
    private String description;
    private boolean visibility;
    private int total;
    private String topic;
    private QuizCreationType quizCreationType;
    private List<QuestionData> questionData;
}
