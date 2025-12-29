package webtech.online.course.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.course.QuizDTO;
import webtech.online.course.dtos.quiz.QuizSubmissionDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.models.Quiz;
import webtech.online.course.models.QuizAttempt;
import webtech.online.course.services.QuizAttemptService;
import webtech.online.course.services.QuizService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizController {
    private final QuizService quizService;
    private final QuizAttemptService quizAttemptService;

    @GetMapping
    public ResponseEntity<WrapperResponse> getQuiz(@RequestParam Long quizId, @RequestParam Long userId) {
        try {
            log.info("get quiz has %d %d".formatted(quizId, userId));
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), quizService.getQuiz(quizId, userId)));
        } catch (Exception ex) {
            throw new BaseError(ex.getMessage());
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<WrapperResponse> submitCourse(@RequestBody QuizSubmissionDTO quizSubmissionDTO) {
        try {
            log.info("get quiz has %s".formatted(quizSubmissionDTO.toString()));
            Long quizAttemptId = quizAttemptService.submitAttempt(quizSubmissionDTO);
            Map<String, Object> resp = quizAttemptService.getResponseAttempts(quizAttemptId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), resp));
        } catch (Exception ex) {
            throw new BaseError(ex.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<WrapperResponse> deleteQuiz(@PathVariable Long id) {
        try {
            log.info("get quiz has %s".formatted(id));
            quizService.delete(id);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), id));
        } catch (Exception ex) {
            throw new BaseError(ex.getMessage());
        }
    }

    @GetMapping("/statistic")
    public ResponseEntity<WrapperResponse> getStatistic(@RequestParam Long userId, @RequestParam Long quizId) {
        try {
            log.info("get userId, %d  quizId %d".formatted(userId, quizId));
            List<Map<String, Object>> resp = quizAttemptService.getStatistic(userId, quizId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), resp));
        } catch (Exception ex) {
            throw new BaseError(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<WrapperResponse> getQuizDetail(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), quizService.getQuizDetail(id)));
        } catch (Exception ex) {
            throw new BaseError(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WrapperResponse> updateQuiz(@PathVariable Long id, @ModelAttribute QuizDTO quizDTO) {
        try {
            Quiz quiz= quizService.updateQuiz(id, quizDTO);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "update successfully %d".formatted(quiz.getId())));
        } catch (Exception ex) {
            throw new BaseError(ex.getMessage());
        }
    }
}
