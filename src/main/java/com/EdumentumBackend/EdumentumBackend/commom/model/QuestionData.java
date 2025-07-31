package com.EdumentumBackend.EdumentumBackend.commom.model;

import com.EdumentumBackend.EdumentumBackend.entity.QuestionType;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionData {
    private Long id;
    private String question;
    private QuestionType questionType;
    private List<Option> options;
}
