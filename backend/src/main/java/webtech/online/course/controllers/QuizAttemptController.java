package webtech.online.course.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.quiz.QuizAttemptDTO;
import webtech.online.course.dtos.quiz.QuizSubmissionDTO;
import webtech.online.course.exceptions.DefaultResponse;
import webtech.online.course.exceptions.ErrorResponse;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.models.QuizAttempt;
import webtech.online.course.services.QuizAttemptService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz-attempt")
public class QuizAttemptController {
    private final QuizAttemptService quizAttemptService;

    @PostMapping("/start")
    public ResponseEntity<WrapperResponse> startAttempt(@RequestBody QuizAttemptDTO dto, HttpServletRequest request) {
        try {
            QuizAttempt attempt = quizAttemptService.startAttempt(dto.quizId(), dto.userId());
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.CREATED.value(),attempt));
        } catch (Exception ex) {
            throw new ErrorResponse(500, ex.getMessage(), request.getRequestURI());
        }
    }


    @GetMapping("/user/{attemptId}")
    public ResponseEntity<WrapperResponse> getUserAttempts(
            @PathVariable Long attemptId) {
        var attempts = quizAttemptService.getResponseAttempts(attemptId);
        return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(),attempts));
    }
}
