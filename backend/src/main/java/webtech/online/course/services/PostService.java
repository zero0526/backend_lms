package webtech.online.course.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import webtech.online.course.dtos.post.CreateCommentDTO;
import webtech.online.course.dtos.post.CreatePostDTO;
import webtech.online.course.dtos.post.PostCommentDTO;
import webtech.online.course.dtos.post.PostDTO;

import java.util.List;

public interface PostService {
    PostDTO createPost(Long userId, CreatePostDTO createPostDTO);

    PostCommentDTO createComment(Long userId, CreateCommentDTO createCommentDTO);

    Page<PostDTO> getPostsByCourse(Long courseId, Pageable pageable);

    Page<PostCommentDTO> getCommentsByPost(Long postId, Pageable pageable);

    Page<PostCommentDTO> getRepliesByComment(Long parentCommentId, Pageable pageable);

    PostDTO updatePost(Long currentUserId, Long postId, CreatePostDTO updatePostDTO);

    void deletePost(Long currentUserId, Long postId);

    PostCommentDTO updateComment(Long currentUserId, Long commentId, CreateCommentDTO updateCommentDTO);

    void deleteComment(Long currentUserId, Long commentId);

    PostDTO togglePin(Long postId);
}
