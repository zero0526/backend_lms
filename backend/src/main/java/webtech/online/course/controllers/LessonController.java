package webtech.online.course.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.course.BaseLessonDTO;
import webtech.online.course.dtos.course.CourseMaterialDTO;
import webtech.online.course.dtos.course.LessonDTO;
import webtech.online.course.dtos.course.QuizDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.DefaultResponse;
import webtech.online.course.exceptions.ErrorResponse;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.services.LessonService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lesson")
public class LessonController {
    private final LessonService lessonService;


    @GetMapping("/details")
    public ResponseEntity<WrapperResponse> getLessonById(@RequestParam(required = false) Long userId, @RequestParam Long lessonId) {
        try{
            webtech.online.course.dtos.Lesson.BaseLessonDTO baseLessonDTO= lessonService.getDetailsLesson(lessonId, userId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), baseLessonDTO));
        }catch (Exception ex){
            throw new BaseError(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultResponse> updateLesson(@PathVariable Long id, @ModelAttribute LessonDTO lessonDTO,
            HttpServletRequest request) {
        try {
            lessonService.update(id, lessonDTO);
            return ResponseEntity.ok(new DefaultResponse(HttpStatus.OK.value()));
        } catch (Exception ex) {
            throw new BaseError(500, ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultResponse> deleteLesson(@PathVariable Long id, HttpServletRequest request) {
        try {
            lessonService.delete(id);
            return ResponseEntity.ok(new DefaultResponse(HttpStatus.OK.value()));
        } catch (Exception ex) {
            throw new BaseError(500, ex.getMessage());
        }
    }

    @PutMapping("/{id}/add-quiz")
    public ResponseEntity<DefaultResponse> addQuiz(@PathVariable Long id, @ModelAttribute QuizDTO quizDTO,
            HttpServletRequest request) {
        try {
            lessonService.addQuiz(id, quizDTO);
            return ResponseEntity.ok(new DefaultResponse(HttpStatus.OK.value()));
        } catch (Exception ex) {
            throw new BaseError(500, ex.getMessage());
        }
    }

    @PutMapping("/add-material/{id}")
    public ResponseEntity<DefaultResponse> addCourseMaterial(@PathVariable Long id,
            @ModelAttribute CourseMaterialDTO courseMaterialDTO, HttpServletRequest request) {
        try {
            lessonService.addCourseMaterial(id, courseMaterialDTO);
            return ResponseEntity.ok(new DefaultResponse(HttpStatus.OK.value()));
        } catch (Exception ex) {
            throw new ErrorResponse(500, ex.getMessage(), request.getRequestURI());
        }
    }
}
