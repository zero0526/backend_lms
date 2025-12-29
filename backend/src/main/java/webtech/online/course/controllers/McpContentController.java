package webtech.online.course.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.course.McqContentDTO;
import webtech.online.course.exceptions.ErrorResponse;
import webtech.online.course.exceptions.WrapperResponse;
import webtech.online.course.services.McqContentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mcp-content")
public class McpContentController {
    private final McqContentService mcqContentService;

    @PutMapping("/{id}")
    public ResponseEntity<WrapperResponse> updateMcpContent(@PathVariable Long id,
            @ModelAttribute McqContentDTO mcqContentDTO, HttpServletRequest request) {
        try {
            mcqContentService.update(id, mcqContentDTO);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "successfully"));
        } catch (Exception ex) {
            throw new ErrorResponse(500, ex.getMessage(), request.getRequestURI());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WrapperResponse> deleteMcpContent(@PathVariable Long id, HttpServletRequest request) {
        try {
            mcqContentService.delete(id);
            return ResponseEntity.ok(new WrapperResponse(HttpStatus.OK.value(), "successfully"));
        } catch (Exception ex) {
            throw new ErrorResponse(500, ex.getMessage(), request.getRequestURI());
        }
    }
}
