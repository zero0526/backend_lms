package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.Lesson;
import webtech.online.course.models.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Query(
            """
                SELECT COALESCE(MAX(qa.totalScore),0)
                FROM QuizAttempt qa
                WHERE qa.quiz.id=:quizId AND qa.user.id=:userId
            """
    )
    Float findMaxScoreAttempt(@Param("userId")Long userId, @Param("quizId")Long quizId);

    @Modifying
    @Query(value = """
              UPDATE lesson_progress lp
              SET progress_quiz =
                  (lp.progress_quiz * x.quizCount - :oldScore + :newScore) / (x.quizCount+1)
              FROM (
                  SELECT COUNT(q.id) AS quizCount
                  FROM lessons l
                  JOIN quizzes q ON q.lesson_id = l.id
                  WHERE l.id = :lessonId
              ) AS x
              WHERE lp.user_id = :userId
                AND lp.lesson_id = :lessonId;
        """, nativeQuery = true)
    void updateProgressLesson(
            @Param("userId") Long userId,
            @Param("lessonId") Long lessonId,
            @Param("oldScore") Double oldScore,
            @Param("newScore") Double newScore
    );

    @Query("""
        SELECT qu FROM Quiz qu
        LEFT JOIN qu.questions q
        LEFT JOIN q.mcpContents mcp
        WHERE qu.id = :id
    """)
    Quiz findFullTree(@Param("id") Long id);
}
