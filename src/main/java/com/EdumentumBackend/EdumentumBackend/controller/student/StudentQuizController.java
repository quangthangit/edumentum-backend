package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.QuizService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/student/quiz")
public class StudentQuizController {

    private final QuizService quizService;
    private final JwtService jwtService;

    public StudentQuizController(QuizService quizService, JwtService jwtService) {
        this.quizService = quizService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> createQuiz(
            @Valid @RequestBody QuizRequestDto quizRequestDto,
            @RequestHeader("Authorization") String authHeader) throws JsonProcessingException {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", "error",
                    "error", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        QuizResponseDto response = quizService.create(jwtService.extractUserId(token), quizRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "message", "Quiz created successfully",
                "data", response
        ));
    }
}
