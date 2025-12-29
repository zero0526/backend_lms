package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import webtech.online.course.dtos.Lesson.BaseDocument;
import webtech.online.course.dtos.Lesson.BaseLessonDTO;
import webtech.online.course.dtos.Lesson.BaseQuizDTO;
import webtech.online.course.models.Chapter;
import webtech.online.course.models.Lesson;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query(value = """
            SELECT json_build_object(
                'lessonId', l.id,
                'urlVideo', v.video_url,
                'order', l.order,
                'title', l.title,
                'lastWatchedAt', COALESCE(lp.last_watched_at_second, 0),
                'description', l.description,
                'duration', v.duration,
                'docs', (
                    SELECT json_agg(json_build_object(
                        'id', cm.id,
                        'docUrl', cm.doc_url,
                        'title', cm.title,
                        'fileType', cm.file_type
                    ))
                    FROM course_materials cm
                    WHERE cm.lesson_id = l.id
                ),
                'quizzes', (
                    SELECT json_agg(json_build_object(
                        'quizId', qz.id,
                        'titleQuiz', qz.title,
                        'description', qz.description,
                        'numOfQuestion', COALESCE(qc.num_questions,0),
                        'level', qz.difficulty_avg,
                        'timeLimit', qz.time_limit_minutes,
                        'attemptCount', COALESCE(qa.attempt_count, 0)
                    ))
                    FROM quizzes qz
                    LEFT JOIN (
                        SELECT q.quiz_id, COUNT(*) AS num_questions
                        FROM questions q
                        GROUP BY q.quiz_id
                    ) qc ON qc.quiz_id = qz.id
                    LEFT JOIN (
                        SELECT qa.quiz_id, COUNT(*) AS attempt_count
                        FROM quiz_attempts qa
                        WHERE (:userId IS NOT NULL AND qa.user_id = :userId)
                        GROUP BY qa.quiz_id
                    ) qa ON qa.quiz_id = qz.id
                    WHERE qz.lesson_id = l.id
                )
            )
            FROM lessons l
            LEFT JOIN videos v ON v.id = l.video_id
            LEFT JOIN lesson_progress lp ON (lp.lesson_id = l.id AND :userId IS NOT NULL AND lp.user_id = :userId)
            WHERE l.id = :lessonId;
            """, nativeQuery = true)
    Object getDetailsLesson(@Param("lessonId") Long lessonId, @Param("userId") Long userId);

    @Query("""
                SELECT l FROM Lesson l
                LEFT JOIN FETCH l.courseMaterials cm
                LEFT JOIN FETCH l.quizzes qu
                LEFT JOIN FETCH qu.questions q
                LEFT JOIN FETCH q.mcpContents mcp
                WHERE l.id = :id
            """)
    Lesson findFullTree(@Param("id") Long id);

    @EntityGraph(attributePaths = {
            "courseMaterials",
            "quizzes",
            "quizzes.questions",
            "quizzes.questions.mcpContents"
    })
    Optional<Lesson> findById(Long lessonId);
}
