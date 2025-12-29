package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import webtech.online.course.dtos.course.AttemptResponseRow;
import webtech.online.course.models.Question;
import webtech.online.course.models.QuizAttempt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserIdAndQuizId(Long userId, Long quizId);

    @Query("""
                select new webtech.online.course.dtos.course.AttemptResponseRow(
                    qa.id,
                    qa.totalScore,
                    q.id,
                    q.questionText,
                    q.questionImg,
                    q.order,
                    q.explanation,
                    qr.isSelected,
                    mcp.isCorrect,
                    qr.scoreAwarded,
                    mcp.choiceText,
                    mcp.choiceImage,
                    count(distinct qc.id)
                )
                from QuizAttempt qa
                join qa.quiz qz
                join qz.questions q
                left join q.mcpContents mcp
                left join qa.questionResponses qr on (qr.question = q and qr.mcpContent = mcp)
                left join q.questionComments qc
                where qa.id = :attemptId

                group by qa.id, qa.totalScore,
                         q.id, q.questionText, q.questionImg, q.order, q.explanation,
                         qr.isSelected, mcp.isCorrect, qr.scoreAwarded, mcp.choiceText, mcp.choiceImage
            """)
    List<AttemptResponseRow> fetchAttemptResponse(@Param("attemptId") Long attemptId);

}
