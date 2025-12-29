package webtech.online.course.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.course.ChapterDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.DefaultResponse;
import webtech.online.course.exceptions.ErrorResponse;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.models.Chapter;
import webtech.online.course.services.ChapterService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chapter")
public class ChapterController {
    private final ChapterService chapterService;

    @GetMapping("/{id}")
    public ResponseEntity<WrapperResponse> getChapterById(@PathVariable Long id) {
        Chapter chapter = chapterService.findById(id);
        return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), chapter));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WrapperResponse> updateChapter(@PathVariable Long id, @RequestBody ChapterDTO chapterDTO,
            HttpServletRequest request) {
        try {
            chapterService.update(id, chapterDTO);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "successfully"));
        } catch (Exception ex) {
            throw new BaseError(500, ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WrapperResponse> deleteChapter(@PathVariable Long id, HttpServletRequest request) {
        try {
            chapterService.delete(id);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "successfully"));
        } catch (Exception ex) {
            throw new BaseError(500, ex.getMessage());
        }
    }
}
