package webtech.online.course.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import webtech.online.course.dtos.post.CreateCommentDTO;
import webtech.online.course.dtos.post.CreatePostDTO;
import webtech.online.course.dtos.post.PostCommentDTO;
import webtech.online.course.dtos.post.PostDTO;
import webtech.online.course.security.UserPrincipal;
import webtech.online.course.services.PostService;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDTO> createPost(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody CreatePostDTO createPostDTO) {
        return ResponseEntity.ok(postService.createPost(currentUser.getId(), createPostDTO));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Page<PostDTO>> getPostsByCourse(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok(postService.getPostsByCourse(courseId, pageable));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long postId,
            @RequestBody CreatePostDTO updatePostDTO) {
        return ResponseEntity.ok(postService.updatePost(currentUser.getId(), postId, updatePostDTO));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long postId) {
        postService.deletePost(currentUser.getId(), postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comments")
    public ResponseEntity<PostCommentDTO> createComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody CreateCommentDTO createCommentDTO) {
        return ResponseEntity.ok(postService.createComment(currentUser.getId(), createCommentDTO));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<Page<PostCommentDTO>> getCommentsByPost(
            @PathVariable Long postId,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.getCommentsByPost(postId, pageable));
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<Page<PostCommentDTO>> getRepliesByComment(
            @PathVariable Long commentId,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(postService.getRepliesByComment(commentId, pageable));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<PostCommentDTO> updateComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long commentId,
            @RequestBody CreateCommentDTO updateCommentDTO) {
        return ResponseEntity.ok(postService.updateComment(currentUser.getId(), commentId, updateCommentDTO));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long commentId) {
        postService.deleteComment(currentUser.getId(), commentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}/pin")
    public ResponseEntity<PostDTO> togglePin(
            @AuthenticationPrincipal UserPrincipal currentUser, // Could verify if instructor here
            @PathVariable Long postId) {
        return ResponseEntity.ok(postService.togglePin(postId));
    }
}
