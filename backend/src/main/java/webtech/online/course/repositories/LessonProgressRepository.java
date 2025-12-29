package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import webtech.online.course.dtos.course.BaseChapterDTO;
import webtech.online.course.dtos.course.BaseLessonDTO;
import webtech.online.course.dtos.course.FlatBaseChapterDTO;
import webtech.online.course.models.LessonProgress;
import webtech.online.course.models.LessonProgressId;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, LessonProgressId> {
    Optional<LessonProgress> findByIdLessonIdAndIdUserId(Long lessonId, Long userId);

    @Query(
            """
            SELECT new webtech.online.course.dtos.course.FlatBaseChapterDTO(
                ch.id,
                ch.title,
                ch.order,
                le.id,
                le.title,
                le.order,
                v.duration,
                COALESCE(lp.progressVideo*0.5 + lp.progressQuiz*0.5, 0.0)
            )
            FROM Course c 
            LEFT JOIN c.chapters ch
            LEFT JOIN  ch.lessons le
            LEFT JOIN  le.video v
            LEFT JOIN le.lessonProgress lp WITH (:userId IS NOT NULL AND lp.user.id = :userId)
            WHERE c.id=:courseId 
            ORDER BY ch.order, le.order
            """
    )
    public List<FlatBaseChapterDTO> getProgress(@Param("userId") Long userId, @Param("courseId") Long courseId);

}