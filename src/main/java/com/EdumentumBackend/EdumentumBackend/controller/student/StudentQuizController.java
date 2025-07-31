package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.service.QuizService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/student/quiz")
public class StudentQuizController {

    private final QuizService quizService;

    public StudentQuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizRequestDto quizRequestDto) throws JsonProcessingException {
        QuizResponseDto response = quizService.create(quizRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "message", "Quiz created successfully",
                "data", response
        ));
    }
}
