package webtech.online.course.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.PostComment;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    Page<PostComment> findByPostIdAndPostCommentIsNull(Long postId, Pageable pageable);

    Page<PostComment> findByPostCommentId(Long parentCommentId, Pageable pageable);

    Page<PostComment> findByPostCourseIdOrderByCreatedAtDesc(Long courseId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM post_comments WHERE post_id = :postId AND parent_comment_id IS NULL", nativeQuery = true)
    long countTopLevelCommentsByPostId(@org.springframework.data.repository.query.Param("postId") Long postId);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.user.id FROM PostComment c WHERE c.post.id = :postId")
    java.util.List<Long> findDistinctUserIdsByPostId(
            @org.springframework.data.repository.query.Param("postId") Long postId);
}
