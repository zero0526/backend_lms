package webtech.online.course.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.video.SegmentDTO;
import webtech.online.course.exceptions.DefaultResponse;
import webtech.online.course.exceptions.ErrorResponse;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.services.SegmentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/segment")
public class SegmentController {
    private final SegmentService segmentService;

    @PostMapping("/{videoId}")
    public ResponseEntity<WrapperResponse> createSegment(@PathVariable Long videoId, @RequestBody SegmentDTO segmentDTO,
                                                         HttpServletRequest request) {
        try {
            segmentService.save(videoId, segmentDTO);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.CREATED.value(), "successfully"));
        } catch (Exception ex) {
            throw new ErrorResponse(500, ex.getMessage(), request.getRequestURI());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<WrapperResponse> updateSegment(@PathVariable Long id, @RequestBody SegmentDTO segmentDTO,
            HttpServletRequest request) {
        try {
            segmentService.update(id, segmentDTO);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "successfully"));
        } catch (Exception ex) {
            throw new ErrorResponse(500, ex.getMessage(), request.getRequestURI());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WrapperResponse> deleteSegment(@PathVariable Long id, HttpServletRequest request) {
        try {
            segmentService.delete(id);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "successfully"));
        } catch (Exception ex) {
            throw new ErrorResponse(500, ex.getMessage(), request.getRequestURI());
        }
    }
}
