package webtech.online.course.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.services.CourseMaterialService;

@RequiredArgsConstructor
@RequestMapping("/api/course-material")
@RestController
@Slf4j
public class CourseMaterialController {
    private final CourseMaterialService courseMaterialService;

    @DeleteMapping("/{id}")
    public ResponseEntity<WrapperResponse> deleteCm(@PathVariable Long id){
        try{
            courseMaterialService.deleteById(id);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "success"));
        }catch (Exception e){
            throw new BaseError(e.getMessage());
        }
    }
}
