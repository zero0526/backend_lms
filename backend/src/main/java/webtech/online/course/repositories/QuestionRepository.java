package webtech.online.course.repositories;

import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import webtech.online.course.dtos.course.CommentQuestionDTO;
import webtech.online.course.models.Question;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("""
        SELECT q FROM Question q
        LEFT JOIN q.mcpContents mcp
        WHERE q.id = :id
    """)
    Question findFullTree(@Param("id") Long id);

    @Query("""
    SELECT new webtech.online.course.dtos.course.CommentQuestionDTO(
        qc.id,
        qc.isEdited,
        qc.updatedAt,
        u.id,
        u.fullName,
        u.pictureUrl,
        COALESCE(COUNT(distinct child.id),0),
        qc.content
    )
    FROM QuestionComment qc
    LEFT JOIN qc.questionComment child
    LEFT JOIN qc.user u
    WHERE qc.question.id = :questionId
    GROUP BY qc.id, qc.isEdited, qc.updatedAt, u.id, u.fullName, u.pictureUrl, qc.content
""")
    List<CommentQuestionDTO> findCommentQuestion(@Param("questionId") Long questionId);
}
