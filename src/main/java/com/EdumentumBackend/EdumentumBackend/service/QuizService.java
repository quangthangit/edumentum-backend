package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.QuizResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface QuizService {
    QuizResponseDto create(Long userId,QuizRequestDto quizRequestDto) throws JsonProcessingException;
}
