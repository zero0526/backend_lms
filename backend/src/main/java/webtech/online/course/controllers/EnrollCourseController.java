package webtech.online.course.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.EnrollCourse.EnrollCourseDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.models.Enrollment;
import webtech.online.course.services.EnrollCourseService;

@RestController
@RequestMapping("/enroll")
@Slf4j
@RequiredArgsConstructor
public class EnrollCourseController {
    private final EnrollCourseService enrollCourseService;

    @PostMapping
    public ResponseEntity<WrapperResponse> enroll(@RequestBody  EnrollCourseDTO enrollCourseDTO){
        try{
             enrollCourseService.enrollment(enrollCourseDTO);
             return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), null));
        }catch (Exception e){
            throw new BaseError(e.getMessage());
        }
    }
}
