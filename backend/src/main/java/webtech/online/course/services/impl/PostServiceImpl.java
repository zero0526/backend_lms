package webtech.online.course.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webtech.online.course.annotations.MentionNotification;
import webtech.online.course.dtos.post.CreateCommentDTO;
import webtech.online.course.dtos.post.CreatePostDTO;
import webtech.online.course.dtos.post.PostCommentDTO;
import webtech.online.course.dtos.post.PostDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.*;
import webtech.online.course.repositories.CourseRepository;
import webtech.online.course.repositories.PostCommentRepository;
import webtech.online.course.repositories.PostRepository;
import webtech.online.course.repositories.UserRepository;
import webtech.online.course.services.PostService;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

        private final PostRepository postRepository;
        private final PostCommentRepository postCommentRepository;
        private final UserRepository userRepository;
        private final CourseRepository courseRepository;
        private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;
        private final webtech.online.course.services.NotificationService notificationService;

        @Override
        @Transactional
        @MentionNotification
        public PostDTO createPost(Long userId, CreatePostDTO createPostDTO) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new BaseError(404, "User not found"));
                Course course = courseRepository.findById(createPostDTO.getCourseId())
                                .orElseThrow(() -> new BaseError(404, "Course not found"));

                Post post = Post.builder()
                                .user(user)
                                .course(course)
                                .title(createPostDTO.getTitle())
                                .content(createPostDTO.getContent())
                                .isPinned(createPostDTO.getIsPinned() != null ? createPostDTO.getIsPinned() : false)
                                .build();

                post = postRepository.save(post);
                return mapToPostDTO(post);
        }

        @Override
        @Transactional
        @MentionNotification
        public PostCommentDTO createComment(Long userId, CreateCommentDTO createCommentDTO) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new BaseError(404, "User not found"));
                Post post = postRepository.findById(createCommentDTO.getPostId())
                                .orElseThrow(() -> new BaseError(404, "Post not found"));

                PostComment parentComment = null;
                if (createCommentDTO.getParentCommentId() != null) {
                        parentComment = postCommentRepository.findById(createCommentDTO.getParentCommentId())
                                        .orElseThrow(() -> new BaseError(404, "Parent comment not found"));
                }

                PostComment comment = PostComment.builder()
                                .user(user)
                                .post(post)
                                .postComment(parentComment)
                                .content(createCommentDTO.getContent())
                                .isEdited(false)
                                .build();

                comment = postCommentRepository.save(comment);
                PostCommentDTO response = mapToPostCommentDTO(comment);

                // Send instant comment update via WebSocket
                messagingTemplate.convertAndSend("/topic/posts/" + post.getId() + "/comments", response);

                // 1. Notify Post Owner
                if (!post.getUser().getId().equals(userId)) {
                        notificationService.createNotification(
                                        post.getUser().getId(),
                                        "New Comment on your Post",
                                        user.getFullName() + " commented on: " + post.getTitle(),
                                        "/forum/" + post.getCourse().getId());
                }

                // 2. Notify other commenters
                java.util.List<Long> commenterIds = postCommentRepository.findDistinctUserIdsByPostId(post.getId());
                for (Long commenterId : commenterIds) {
                        if (!commenterId.equals(userId) && !commenterId.equals(post.getUser().getId())) {
                                notificationService.createNotification(
                                                commenterId,
                                                "New Comment on shared post",
                                                user.getFullName() + " also commented on a post you followed",
                                                "/forum/" + post.getCourse().getId());
                        }
                }

                return response;
        }

        @Override
        @Transactional
        public Page<PostDTO> getPostsByCourse(Long courseId, Pageable pageable) {
                Page<Post> posts = postRepository.findByCourseIdOrderByIsPinnedDescCreatedAtDesc(courseId, pageable);
                return posts.map(this::mapToPostDTO);
        }

        @Override
        @Transactional
        public Page<PostCommentDTO> getCommentsByPost(Long postId, Pageable pageable) {
                Page<PostComment> comments = postCommentRepository.findByPostIdAndPostCommentIsNull(postId, pageable);
                return comments.map(this::mapToPostCommentDTO);
        }

        @Override
        @Transactional
        public Page<PostCommentDTO> getRepliesByComment(Long parentCommentId, Pageable pageable) {
                Page<PostComment> replies = postCommentRepository.findByPostCommentId(parentCommentId, pageable);
                return replies.map(this::mapToPostCommentDTO);
        }

        @Override
        @Transactional
        public PostDTO updatePost(Long currentUserId, Long postId, CreatePostDTO updatePostDTO) {
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new BaseError(404, "Post not found"));
                if (!post.getUser().getId().equals(currentUserId)) {
                        throw new BaseError(403, "You do not have permission to update this post");
                }
                post.setTitle(updatePostDTO.getTitle());
                post.setContent(updatePostDTO.getContent());
                if (updatePostDTO.getIsPinned() != null) {
                        post.setIsPinned(updatePostDTO.getIsPinned());
                }
                post.setUpdateAt(LocalDateTime.now());
                post = postRepository.save(post);
                return mapToPostDTO(post);
        }

        @Override
        @Transactional
        public void deletePost(Long currentUserId, Long postId) {
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new BaseError(404, "Post not found"));
                if (!post.getUser().getId().equals(currentUserId)) {
                        throw new BaseError(403, "You do not have permission to delete this post");
                }
                postRepository.delete(post);
        }

        @Override
        @Transactional
        public PostCommentDTO updateComment(Long currentUserId, Long commentId, CreateCommentDTO updateCommentDTO) {
                PostComment comment = postCommentRepository.findById(commentId)
                                .orElseThrow(() -> new BaseError(404, "Comment not found"));
                if (!comment.getUser().getId().equals(currentUserId)) {
                        throw new BaseError(403, "You do not have permission to update this comment");
                }
                comment.setContent(updateCommentDTO.getContent());
                comment.setIsEdited(true);
                comment.setUpdatedAt(LocalDateTime.now());
                comment = postCommentRepository.save(comment);
                return mapToPostCommentDTO(comment);
        }

        @Override
        @Transactional
        public void deleteComment(Long currentUserId, Long commentId) {
                PostComment comment = postCommentRepository.findById(commentId)
                                .orElseThrow(() -> new BaseError(404, "Comment not found"));
                if (!comment.getUser().getId().equals(currentUserId)) {
                        throw new BaseError(403, "You do not have permission to delete this comment");
                }
                postCommentRepository.delete(comment);
        }

        @Override
        @Transactional
        public PostDTO togglePin(Long postId) {
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new BaseError(404, "Post not found"));
                post.setIsPinned(!post.getIsPinned());
                post = postRepository.save(post);
                return mapToPostDTO(post);
        }

        private PostDTO mapToPostDTO(Post post) {
                long commentsCount = postCommentRepository.countTopLevelCommentsByPostId(post.getId());
                return PostDTO.builder()
                                .id(post.getId())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .userId(post.getUser().getId())
                                .userName(post.getUser().getFullName())
                                .userAvatar(post.getUser().getPictureUrl())
                                .createdAt(post.getCreatedAt())
                                .commentsCount(commentsCount)
                                .isPinned(post.getIsPinned())
                                .build();
        }

        private PostCommentDTO mapToPostCommentDTO(PostComment comment) {
                long repliesCount = comment.getReplies() != null ? comment.getReplies().size() : 0;
                return PostCommentDTO.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .userId(comment.getUser().getId())
                                .userName(comment.getUser().getFullName())
                                .userAvatar(comment.getUser().getPictureUrl())
                                .createdAt(comment.getCreatedAt())
                                .isEdited(comment.getIsEdited())
                                .repliesCount(repliesCount)
                                .replies(new ArrayList<>())
                                .build();
        }
}
