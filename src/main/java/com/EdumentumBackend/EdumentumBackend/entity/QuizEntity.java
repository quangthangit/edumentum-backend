package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "quizzes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_question_id", referencedColumnName = "quizQuestionId")
    private QuizQuestionEntity quizQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
