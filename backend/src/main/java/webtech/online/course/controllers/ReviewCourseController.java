package webtech.online.course.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.course.ReviewCourseDTO;
import webtech.online.course.dtos.course.ReviewCourseRep;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.services.ReviewCourseService;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@Slf4j
public class ReviewCourseController {
    private final ReviewCourseService reviewCourseService;

    @PostMapping("/review-course")
    public ResponseEntity<WrapperResponse> reviewCourse(@RequestBody ReviewCourseDTO reviewCourseDTO){
        try{
            reviewCourseService.saveReviewCourse(reviewCourseDTO);
            return ResponseEntity.ok(WrapperResponse.builder().status(HttpStatus.CREATED.value()).build());
        }catch (Exception ex){
            log.error("Error at course/review get {} cause {}",reviewCourseDTO.toString(), ex.getMessage());
            throw new BaseError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        }
    }
    @GetMapping("/review-course")
    public ResponseEntity<WrapperResponse> getReviewCourse(@RequestParam Long courseId, @RequestParam int pageNum, @RequestParam int limit){
        try{
            Pageable pageable= PageRequest.of(pageNum, limit);
            Page<ReviewCourseRep> rc= reviewCourseService.findByCourseId(courseId, pageable);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), rc));
        }catch (Exception e){
            log.error("Error raised at review-course get method with courseId {} cause {}", courseId, e.getMessage());
            throw new BaseError(e.getMessage());
        }
    }
}
