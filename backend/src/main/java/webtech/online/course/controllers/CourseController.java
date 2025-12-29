package webtech.online.course.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.course.*;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.models.*;
import webtech.online.course.services.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
@Slf4j
public class CourseController {
    private final CourseService courseService;
    private final LessonService lessonService;
    private final ChapterService chapterService;
    private final UserService userService;
    private final LessonProgressService lessonProgressService;
    private final CourseSearchEngineService courseSearchEngineService;

    @PostMapping(value = "/post")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<WrapperResponse> postCourse(@ModelAttribute CourseDTO courseDTO, HttpServletRequest request) {
        try {
            log.info("get {} {}", courseDTO.thumbnailUrl(), courseDTO.tags());
            Course course = courseService.save(Course.builder()
                    .title(courseDTO.title())
                    .description(courseDTO.desc())
                            .courseTarget(String.join("||",courseDTO.courseTarget()))
                            .precondition(String.join("||",courseDTO.precondition()))
                    .build(),courseDTO);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.CREATED.value(),Map.of("id", course.getId())));
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new BaseError(500, ex.getMessage());
        }
    }

    @GetMapping("/develop_course/{instructorId}")
    public ResponseEntity<WrapperResponse>  getDevCourse(@PathVariable Long instructorId, HttpServletRequest request){
        try{
            List<MyDevCourseDTO> myDevCourseDTOS = courseService.getMyDevCourse(instructorId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), myDevCourseDTOS));
        }catch (Exception e){
            throw new BaseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
    @GetMapping("/completed/{instructorId}")
    public ResponseEntity<WrapperResponse>  getCompletedCourse(@PathVariable Long instructorId, HttpServletRequest request){
        try{
            List<MyDevCourseDTO> myDevCourseDTOS = courseService.getMyCompletedCourse(instructorId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), myDevCourseDTOS));
        }catch (Exception e){
            throw new BaseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
    @GetMapping("/all_dev/{instructorId}")
    public ResponseEntity<WrapperResponse>  getAllCreatedCourse(@PathVariable Long instructorId, HttpServletRequest request){
        try{
            List<MyAllCourseDTO> myDevCourseDTOS = courseService.getAllCreatedCourse(instructorId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), myDevCourseDTOS));
        }catch (Exception e){
            throw new BaseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
    @PostMapping("/add-chapter")
    public ResponseEntity<WrapperResponse> addChapter(@RequestBody ChapterDTO chapterDTO, HttpServletRequest request) {
        try {
            Chapter chapter= courseService.addNewChapter(chapterDTO.courseId(), Chapter.builder()
                    .order(chapterDTO.order())
                    .title(chapterDTO.title())
                    .build());
            chapter= chapterService.save(chapter);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), Map.of("id", chapter.getId())));
        } catch (Exception ex) {
            throw new BaseError(500, ex.getMessage());
        }
    }

    @PostMapping("/add-lesson")
    public ResponseEntity<WrapperResponse> addLesson(@ModelAttribute LessonDTO lessonDTO, HttpServletRequest request) throws IOException {
         try{
             log.debug("get req create lesson{}", lessonDTO.title());
            Lesson lesson = lessonService.insert(lessonDTO);
            lesson= chapterService.addLesson(lessonDTO.chapterId(), lesson);
            lesson = lessonService.save(lesson);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), Map.of("id", lesson.getId())));
         }catch (Exception ex){
             log.error(ex.getMessage());
            throw new BaseError(500, ex.getMessage());
         }
    }
    @GetMapping("/{userId}")
    public ResponseEntity<WrapperResponse> getMyCourse(@PathVariable Long userId){
        try{
            List<MyCourseDTO> myCourseDTOS = courseService.getMyCourse(userId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), myCourseDTOS));
        }catch (Exception e){
            throw new BaseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

//update them sau khi submit quizz
    @PostMapping("/update-progress")
    public ResponseEntity<WrapperResponse> updateProgress(@RequestBody VideoProgressDTO videoProgressDTO){
        try{
            lessonProgressService.commit(videoProgressDTO);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "successful"));
        }catch (Exception e){
            throw new BaseError(500, e.getMessage());
        }
    }
    @GetMapping("/recommend/{userId}")
    public ResponseEntity<WrapperResponse> recommend(@PathVariable Long userId){
        try{
            List<RecommendedCourseDTO> recommendedCourseDTOs= courseService.recommendCourse(userId, 10);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), recommendedCourseDTOs));
        }catch (Exception e){
            throw new BaseError(500, e.getMessage());
        }
    }
    @PostMapping("/search")
    public ResponseEntity<WrapperResponse> search(
            @RequestBody CourseSearchReq courseSearchReq
    ) {
        try{
           List<SearchedCourseRes> searchedCourseRes= courseService.findSearchedCourse(courseSearchReq);
           return ResponseEntity.ok(new WrapperResponse(200, searchedCourseRes ));
        }catch (Exception e){
            throw new BaseError(e.getMessage());
        }
    }
    @GetMapping("/search/auto-complete")
    public ResponseEntity<WrapperResponse> autocomplete(@RequestParam String keyword, @RequestParam(defaultValue = "10") int limit){
        try{
            List<String> suggestions= courseSearchEngineService.autoCompleteCourseTitle(keyword, limit);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), suggestions));
        }catch (Exception ex){
            throw new BaseError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WrapperResponse> updateCourse(@PathVariable Long id, @ModelAttribute CourseUpdateDTO courseDTO,
            HttpServletRequest request) {
        try {
            courseService.update(id, courseDTO);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(),"successfully"));
        } catch (Exception ex) {
            throw new BaseError(500, ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WrapperResponse> deleteCourse(@PathVariable Long id, HttpServletRequest request) {
        try {
            courseService.delete(id);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(),"successfully"));
        } catch (Exception ex) {
            throw new BaseError(500, ex.getMessage());
        }
    }
    @GetMapping(value = {"/details", "/details/{courseId}"})
    public ResponseEntity<WrapperResponse> getDetailsCourse(@PathVariable(required = false) Long courseId){
        try{
            DetailsCourseDTO detailsCourseDTO = courseService.getDetailsCourse(courseId);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), detailsCourseDTO));
        }catch (Exception e){
            throw new BaseError(e.getMessage());
        }
    }
    @GetMapping("/outline")
    public ResponseEntity<WrapperResponse> getOutlineCourse(@RequestParam(required = false) Long userId, @RequestParam Long courseId){
        try{
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), lessonProgressService.getProgress(userId, courseId)));
        }catch (Exception e){
            throw new BaseError(HttpStatus.BAD_REQUEST.value(),e.getMessage());
        }
    }

    @GetMapping("/introduce-course")
    public ResponseEntity<WrapperResponse> introduce(@RequestParam(required = false) List<String> tags, @RequestParam(defaultValue = "0.0")  Double lowerBoundRating,@RequestParam(defaultValue = "createdAt")  String sortBy, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int page){
        try{
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), courseService.getIntroduceCourse(tags, lowerBoundRating, sortBy, limit, page)));
        }catch (Exception e){
            throw new BaseError(HttpStatus.BAD_REQUEST.value(),e.getMessage());
        }
    }

    @GetMapping("/course-tags")
    public ResponseEntity<WrapperResponse> tagCourse(@RequestParam(defaultValue = "8") int limit){
        try{
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), courseService.getPopularTags(limit)));
        }catch (Exception e){
            throw new BaseError(HttpStatus.BAD_REQUEST.value(),e.getMessage());
        }
    }
}
