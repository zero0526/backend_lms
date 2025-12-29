package webtech.online.course.services;

import webtech.online.course.dtos.course.QuizDTO;
import webtech.online.course.dtos.quiz.QuizDetailDTO;
import webtech.online.course.models.Quiz;

import java.util.Map;

public interface QuizService {
    public Quiz uploadQuiz(QuizDTO quizDTO);

    public Map<String, Object> getQuiz(Long quizId, Long userId);

    public void delete(Long id);

    QuizDetailDTO getQuizDetail(Long id);

    Quiz updateQuiz(Long id, QuizDTO quizDTO);
}
