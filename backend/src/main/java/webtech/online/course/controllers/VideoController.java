package webtech.online.course.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.video.VideoDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.DefaultResponse;
import webtech.online.course.exceptions.ErrorResponse;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.services.VideoService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video")
public class  VideoController {
    private final VideoService videoService;

    @PostMapping
    public ResponseEntity<WrapperResponse> uploadVideo(@ModelAttribute VideoDTO videoDTO, HttpServletRequest request) {
        try {
            videoService.uploadVideo(videoDTO);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.CREATED.value(), "created"));
        } catch (Exception ex) {
            throw new ErrorResponse(500, ex.getMessage(), request.getRequestURI());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<WrapperResponse> getVideoById(@PathVariable Long id) {
        return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), ""));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WrapperResponse> updateVideo(@PathVariable Long id, @ModelAttribute VideoDTO videoDTO,
            HttpServletRequest request) {
        try {
            videoService.update(id, videoDTO);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "successfully"));
        } catch (Exception ex) {
            throw new ErrorResponse(500, ex.getMessage(), request.getRequestURI());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<WrapperResponse> deleteCm(@PathVariable Long id){
        try{
            videoService.deleteAllByIds(List.of(id));
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "success"));
        }catch (Exception e){
            throw new BaseError(e.getMessage());
        }
    }
}
