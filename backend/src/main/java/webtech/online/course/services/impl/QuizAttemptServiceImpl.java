package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import webtech.online.course.dtos.course.AttemptResponseRow;
import webtech.online.course.dtos.quiz.AnswerSubmissionDTO;
import webtech.online.course.dtos.quiz.QuizSubmissionDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.*;
import webtech.online.course.repositories.*;
import webtech.online.course.services.QuizAttemptService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizAttemptServiceImpl implements QuizAttemptService {
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuestionResponseRepository questionResponseRepository;
    private final QuestionRepository questionRepository;
    private final McqContentRepository mcqContentRepository;
    private final webtech.online.course.services.NotificationService notificationService;

    @Override
    @Transactional
    public QuizAttempt startAttempt(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new BaseError(404, "Quiz not found with id=" + quizId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseError(404, "User not found with id=" + userId));

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setIsCompleted(false);

        return quizAttemptRepository.saveAndFlush(attempt);
    }

    @Override
    @Transactional
    public Long submitAttempt(QuizSubmissionDTO submission) {
        QuizAttempt attempt = quizAttemptRepository.findById(submission.attemptId())
                .orElseThrow(() -> new BaseError(404, "Quiz attempt not found with id=" + submission.attemptId()));

        if (attempt.getIsCompleted()) {
            throw new BaseError(400, "Quiz attempt already completed");
        }

        float totalScore = 0f;

        for (AnswerSubmissionDTO answer : submission.answers()) {
            Question question = questionRepository.findById(answer.questionId())
                    .orElseThrow(() -> new BaseError(404, "Question not found with id=" + answer.questionId()));

            List<MCPContent> choices = question.getMcpContents();
            long numCorrect = choices.stream().filter(MCPContent::getIsCorrect).count();
            float baseScore = (numCorrect > 0 && question.getScore() != null) ? question.getScore() / numCorrect : 0;
            for (MCPContent choice : choices) {
                boolean isSelected = answer.selectedChoiceIds().contains(choice.getId());
                float scoreAwarded = 0f;

                if (isSelected) {
                    scoreAwarded = choice.getIsCorrect() ? baseScore
                            : -2 * baseScore;
                }

                totalScore += scoreAwarded;

                QuestionResponse response = QuestionResponse.builder()
                        .quizAttempt(attempt)
                        .question(question)
                        .isSelected(isSelected)
                        .isCorrect(choice.getIsCorrect())
                        .scoreAwarded(scoreAwarded)
                        .build();
                choice.addQuestionResponse(response);
                questionResponseRepository.save(response);
            }
        }
        attempt.setTotalScore(Math.max(totalScore, 0f));
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setIsCompleted(true);
        QuizAttempt quizAttempt = quizAttemptRepository.saveAndFlush(attempt);
        Quiz quiz = quizAttempt.getQuiz();
        updateLessonTestProgress(attempt.getUser().getId(), quiz, totalScore);

        // Notify Student about Grading
        notificationService.createNotification(
                attempt.getUser().getId(),
                "Quiz Graded",
                "Your results for " + quiz.getTitle() + " are now available. Score: " + totalScore,
                "/student/quiz-result/" + quizAttempt.getId());

        return quizAttempt.getId();
    }

    public void updateLessonTestProgress(Long userId, Quiz quiz, Float score) {
        Float maxScore = quizRepository.findMaxScoreAttempt(userId, quiz.getId());
        float currentMax = (maxScore != null) ? maxScore : 0.0f;
        if (score > currentMax) {
            Lesson lesson = quiz.getLesson();
            quizRepository.updateProgressLesson(userId, lesson.getId(), currentMax * 1.0, score * 1.0);
        }
    }

    @Override
    public QuizAttempt getAttemptById(Long attemptId) {
        return quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new BaseError(404, "Quiz attempt not found with id=" + attemptId));
    }

    @Override
    @Transactional
    public Map<String, Object> getResponseAttempts(Long attemptId) {
        try {
            List<AttemptResponseRow> rows = quizAttemptRepository.fetchAttemptResponse(attemptId);

            if (rows == null || rows.isEmpty()) {
                throw new BaseError(404, "Không tìm thấy kết quả làm bài cho ID: " + attemptId);
            }

            Float totalScore = rows.get(0).getTotalScore();
            // Dùng LinkedHashMap để giữ đúng thứ tự câu hỏi
            Map<Long, Map<String, Object>> questionMap = new LinkedHashMap<>();

            for (AttemptResponseRow r : rows) {
                // 1. Lấy hoặc tạo mới Object câu hỏi
                Map<String, Object> question = questionMap.computeIfAbsent(r.getQuestionId(), id -> {
                    Map<String, Object> q = new HashMap<>();
                    q.put("qId", id);
                    q.put("qText", r.getQuestionText());
                    q.put("qImage", r.getQuestionImg());
                    q.put("order", r.getQuestionOrder());
                    q.put("explain", r.getExplanation());
                    q.put("commentCount", r.getNumOfComment());
                    q.put("choices", new ArrayList<Map<String, Object>>()); // Tạo danh sách chứa các lựa chọn
                    return q;
                });

                // 2. Ép kiểu và thêm LỰA CHỌN hiện tại vào danh sách choices của câu hỏi đó
                List<Map<String, Object>> choices = (List<Map<String, Object>>) question.get("choices");
                Map<String, Object> choiceMap = new HashMap<>();
                choiceMap.put("choiceText", r.getChoiceText() != null ? r.getChoiceText() : "");
                choiceMap.put("choiceImage", r.getChoiceImage() != null ? r.getChoiceImage() : "");
                choiceMap.put("isSelected", r.getIsSelected() != null ? r.getIsSelected() : false);
                choiceMap.put("isCorrect", r.getIsCorrect() != null ? r.getIsCorrect() : false);
                choiceMap.put("score", r.getScoreAwarded() != null ? r.getScoreAwarded() : 0.0f);
                choices.add(choiceMap);
            }

            return Map.of(
                    "totalScore", totalScore != null ? totalScore : 0f,
                    "questions", new ArrayList<>(questionMap.values()));

        } catch (Exception e) {
            // In lỗi ra console để bạn nhìn thấy dòng nào sai
            e.printStackTrace();
            // Trả về một Map chứa lỗi để Frontend hiển thị được
            return Map.of(
                    "error", "Internal Server Error",
                    "message", e.getMessage(),
                    "detail", e.getClass().getSimpleName());
        }
    }

    @Override
    @Transactional
    public List<Map<String, Object>> getStatistic(Long userId, Long quizId) {
        List<QuizAttempt> quizAttempts = quizAttemptRepository.findByUserIdAndQuizId(userId, quizId);

        return quizAttempts.stream()
                .map(qa -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("attemptId", qa.getId());
                    map.put("submittedAt", qa.getSubmittedAt());
                    map.put("totalScore", qa.getTotalScore());
                    map.put("createdAt", qa.getStartedAt());
                    return map;
                })
                .toList();
    }

}
