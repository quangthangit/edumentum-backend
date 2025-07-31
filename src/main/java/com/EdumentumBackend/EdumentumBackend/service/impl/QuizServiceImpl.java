package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.commom.model.QuestionData;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.QuizEntity;
import com.EdumentumBackend.EdumentumBackend.entity.QuizQuestionEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.QuizQuestionRepository;
import com.EdumentumBackend.EdumentumBackend.repository.QuizRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.QuizService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    public QuizServiceImpl(QuizRepository quizRepository, ObjectMapper objectMapper, QuizQuestionRepository quizQuestionRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.quizQuestionRepository = quizQuestionRepository;
    }

    @Override
    @Transactional
    public QuizResponseDto create(Long userId, QuizRequestDto quizRequestDto) throws JsonProcessingException {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        String questionJson = objectMapper.writeValueAsString(quizRequestDto.getQuestions());

        QuizQuestionEntity quizQuestion = QuizQuestionEntity.builder()
                .questions(questionJson)
                .build();

        quizQuestion = quizQuestionRepository.save(quizQuestion);

        QuizEntity quiz = QuizEntity.builder()
                .title(quizRequestDto.getTitle())
                .description(quizRequestDto.getDescription())
                .visibility(quizRequestDto.isVisibility())
                .total(quizRequestDto.getTotal())
                .user(user)
                .topic(quizRequestDto.getTopic())
                .quizCreationType(quizRequestDto.getQuizCreationType())
                .quizQuestion(quizQuestion)
                .build();

        QuizEntity savedQuiz = quizRepository.save(quiz);
        UserResponseDto userDto = UserResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();

        List<QuestionData> questionDataList = quizRequestDto.getQuestions();

        return QuizResponseDto.builder()
                .quizId(savedQuiz.getQuizId())
                .title(savedQuiz.getTitle())
                .description(savedQuiz.getDescription())
                .visibility(savedQuiz.isVisibility())
                .total(savedQuiz.getTotal())
                .topic(savedQuiz.getTopic())
                .userResponseDto(userDto)
                .quizCreationType(savedQuiz.getQuizCreationType())
                .questionData(questionDataList)
                .build();
    }
}
