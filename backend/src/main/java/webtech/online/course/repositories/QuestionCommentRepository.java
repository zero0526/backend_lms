package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import webtech.online.course.dtos.course.CommentQuestionDTO;
import webtech.online.course.models.QuestionComment;

import java.util.List;

@Repository
public interface QuestionCommentRepository extends JpaRepository<QuestionComment, Long> {
    List<QuestionComment> findByQuestionIdAndQuestionCommentIsNull(Long questionId);

    List<QuestionComment> findByQuestionCommentId(Long parentCommentId);

    List<QuestionComment> findByQuestionId(Long questionId);

    @Query(value = """
        SELECT 
            qc.id AS id, 
            qc.is_edited AS isEdited, 
            qc.updated_at AS updatedAt, 
            u.id AS userId, 
            u.full_name AS fullName, 
            u.avatar_url AS pictureUrl,
            (SELECT COUNT(*) FROM question_comments child WHERE child.parent_comment_id = qc.id) AS childCount,
            qc.content AS content
        FROM question_comments qc
        JOIN users u ON qc.user_id = u.id
        WHERE qc.question_id = :questionId 
          AND qc.parent_comment_id IS NULL
        ORDER BY qc.created_at DESC
    """, nativeQuery = true)
    List<Object[]> findTopLevelCommentsWithChildCountByQuestionId(@Param("questionId") Long questionId);

    @Query("""
            SELECT new webtech.online.course.dtos.course.CommentQuestionDTO(
                qc.id,
                qc.isEdited,
                qc.updatedAt,
                u.id,
                u.fullName,
                u.pictureUrl,
                (SELECT COUNT(child) FROM QuestionComment child WHERE child.questionComment = qc),
                qc.content
            )
            FROM QuestionComment qc
            JOIN qc.user u
            WHERE qc.questionComment.id = :questionCommentId
            ORDER BY qc.createdAt ASC
            """)
    List<CommentQuestionDTO> getQuestionComment(@Param("questionCommentId") Long questionCommentId);
}
