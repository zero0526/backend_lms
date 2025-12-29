package webtech.online.course.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.course.CommentQuestionDTO;
import webtech.online.course.dtos.quiz.QuestionCommentDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.DefaultResponse;
import webtech.online.course.exceptions.ErrorResponse;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.models.QuestionComment;
import webtech.online.course.services.QuestionCommentService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question-comment")
public class QuestionCommentController {
    private final QuestionCommentService questionCommentService;

    @PostMapping("/{userId}")
    public ResponseEntity<WrapperResponse> addComment(
            @RequestBody QuestionCommentDTO dto,
            @PathVariable Long userId,
            HttpServletRequest request) {
        try {
            QuestionComment comment = questionCommentService.addComment(dto, userId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.CREATED.value(), Map.of(
                    "comment", comment.getContent(),
                    "id", comment.getId())));
        } catch (Exception ex) {
            throw new BaseError(500, ex.getMessage());
        }
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<WrapperResponse> getQuestionComments(@PathVariable Long questionId) {
        try {
            List<CommentQuestionDTO> comments = questionCommentService.getQuestionComments(questionId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), comments));
        } catch (Exception e) {
            throw new BaseError(e.getMessage());
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<WrapperResponse> updateComment(
            @PathVariable Long commentId,
            @RequestParam String content,
            @RequestParam Long userId,
            HttpServletRequest request) {
        try {
            QuestionComment comment = questionCommentService.updateComment(commentId, content, userId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), Map.of(
                    "comment", comment.getContent(),
                    "id", comment.getId())));
        } catch (Exception ex) {
            throw new ErrorResponse(500, ex.getMessage(), request.getRequestURI());
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<WrapperResponse> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId,
            HttpServletRequest request) {
        try {
            questionCommentService.deleteComment(commentId, userId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "Comment deleted"));
        } catch (Exception ex) {
            throw new ErrorResponse(500, ex.getMessage(), request.getRequestURI());
        }
    }

    @GetMapping("/question-comment/{questionCommentId}")
    public ResponseEntity<WrapperResponse> getChildComment(@PathVariable Long questionCommentId,
            HttpServletRequest request) {
        try {
            List<CommentQuestionDTO> commentQuestionDTOS = questionCommentService.getQuestionComment(questionCommentId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), commentQuestionDTOS));
        } catch (Exception ex) {
            throw new BaseError(500, ex.getMessage());
        }
    }
}
