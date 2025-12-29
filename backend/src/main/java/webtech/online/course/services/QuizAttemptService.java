package webtech.online.course.services;

import webtech.online.course.dtos.quiz.QuizSubmissionDTO;
import webtech.online.course.models.QuizAttempt;

import java.util.List;
import java.util.Map;

public interface QuizAttemptService {
    QuizAttempt startAttempt(Long quizId, Long userId);

    Long submitAttempt(QuizSubmissionDTO submission);

    QuizAttempt getAttemptById(Long attemptId);

    public Map<String, Object> getResponseAttempts(Long attemptId);
    public List<Map<String, Object>>  getStatistic(Long userId, Long quizId);

}
