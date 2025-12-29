package webtech.online.course.repositories;

import jakarta.persistence.*;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import webtech.online.course.dtos.EnrollCourse.CourseStatistic;
import webtech.online.course.dtos.course.*;
import webtech.online.course.models.*;
import org.springframework.data.jpa.repository.EntityGraph;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query(
            """
            SELECT c.id, AVG(rc.rating)
            FROM Course c
            JOIN ReviewCourse rc ON rc.course.id=c.id
            WHERE c.id not in (
                SELECT e.course.id
                FROM Enrollment e
                WHERE e.user.id= :userId
            )
            GROUP BY c.id
            HAVING AVG(rc.rating) > :userBias
            """
    )
    public Map<String, Object> findExpectedCourse(@PathParam("userId") Long userId, @PathParam("meanGlobal") Double meanGlobal, @PathParam("mg") Double userBias);

    @Query("""
        SELECT new webtech.online.course.dtos.course.RecommendedCourseDTO(
            c.id,
            c.thumbnailUrl,
            c.title,
            AVG(rc.rating),
            c.description,
            COUNT(DISTINCT e),
            COUNT(DISTINCT ch),
            c.isCompleted
        )
        FROM Course c
        LEFT JOIN c.reviewCourses rc
        LEFT JOIN c.enrollments e
        LEFT JOIN c.chapters ch
        WHERE c.id = :courseId
        GROUP BY c.id, c.thumbnailUrl, c.description, c.title, c.isCompleted
    """)
    public RecommendedCourseDTO findRecommendCourse(@Param("courseId") Long courseId);

    @Query("""
        SELECT new webtech.online.course.dtos.course.MyCourseDTO(
            c.id,
            c.thumbnailUrl,
            c.title,
            c.description,
            (
                SELECT COALESCE(
                    AVG(COALESCE(lp.progressVideo, 0) * 0.5 + COALESCE(lp.progressQuiz, 0) * 0.5),
                    0.0
                )
                FROM LessonProgress lp
                WHERE lp.course.id = c.id AND lp.user.id = e.user.id
            ),
            (
                SELECT COALESCE(AVG(rc.rating), 0)
                FROM ReviewCourse rc
                WHERE rc.course.id = c.id
            ),
            (
                SELECT COUNT(en)
                FROM Enrollment en
                WHERE en.course.id = c.id
            ),
            (
                SELECT COUNT(ch)
                FROM Chapter ch
                WHERE ch.course.id = c.id
            ),
            c.instructor.fullName,
            c.isCompleted
        )
        FROM Enrollment e
        JOIN e.course c
        WHERE e.user.id = :userId
    """)
    List<MyCourseDTO> findMyCourse(@Param("userId") Long userId);



    @Query(
            """
            SELECT c.title FROM Course c        
            """
    )
    public List<String> getAllCourseTitles();


    @Query("""
    SELECT new webtech.online.course.dtos.course.SearchedCourseRes(
        c.id,
        c.title,
        c.createdAt,
        (SELECT COUNT(DISTINCT e.id) FROM Enrollment e WHERE e.course.id = c.id),
        c.description,
        c.thumbnailUrl,
        (SELECT AVG(rc.rating) FROM ReviewCourse rc WHERE rc.course.id = c.id),
        u.fullName,
        c.isCompleted
    )
    FROM Course c
    JOIN c.instructor u
    WHERE c.id IN :ids
    """)
    List<SearchedCourseRes> findSearchCourseByIds(@Param("ids") List<Long> ids);

    @Query("""
        SELECT new webtech.online.course.dtos.course.DetailsCourseDTO(
            c.id, c.thumbnailUrl,
            COUNT(DISTINCT ch.id),
            COUNT(DISTINCT l.id),
            (SELECT COALESCE(SUM(le.video.duration), 0) FROM Lesson le WHERE le.chapter.course.id = c.id),
            (SELECT AVG(rv.rating) FROM ReviewCourse rv WHERE rv.course.id = c.id),
            (SELECT COUNT(rv.id) FROM ReviewCourse rv WHERE rv.course.id = c.id),
            c.title,
            c.description,
            c.precondition,
            c.courseTarget,
            c.instructor.fullName,
            c.instructor.pictureUrl,
            c.instructor.id,
            c.isCompleted
        )
        FROM Course c
        LEFT JOIN c.chapters ch
        LEFT JOIN ch.lessons l
        WHERE c.id = :courseId
        GROUP BY c.id, c.thumbnailUrl, c.title, c.description, 
                 c.precondition, c.courseTarget,
                 c.instructor.fullName, c.instructor.pictureUrl, c.instructor.id, c.isCompleted
    """)
    DetailsCourseDTO findDetailsCourse(@Param("courseId") Long courseId);


    @Query("""
        SELECT new webtech.online.course.dtos.course.MyDevCourseDTO(
            c.id,
            c.thumbnailUrl,
            c.title,
            c.description,
            COALESCE((SELECT AVG(rc.rating) FROM ReviewCourse rc WHERE rc.course.id = c.id), 0),
            (SELECT COUNT(e.id) FROM Enrollment e WHERE e.course.id = c.id),
            (SELECT COUNT(ch.id) FROM Chapter ch WHERE ch.course.id = c.id)
        )
        FROM Course c
        WHERE c.instructor.id = :instructorId
          AND c.isCompleted = :isCompleted
    """
    )
    List<MyDevCourseDTO> findMyDevCourse(@Param("instructorId") Long instructorId, @Param("isCompleted") Boolean isCompleted);

    @Query("""
        SELECT new webtech.online.course.dtos.course.MyAllCourseDTO(
            c.id,
            c.thumbnailUrl,
            c.title,
            c.description,
            COALESCE((SELECT AVG(rc.rating) FROM ReviewCourse rc WHERE rc.course.id = c.id), 0),
            COALESCE((SELECT COUNT(e.id) FROM Enrollment e WHERE e.course.id = c.id), 0),
            COALESCE((SELECT COUNT(ch.id) FROM Chapter ch WHERE ch.course.id = c.id),0),
            c.isCompleted
        )
        FROM Course c
        WHERE c.instructor.id = :instructorId
        """
    )
    List<MyAllCourseDTO> getAllCreatedCourse(@Param("instructorId") Long instructorId);

    @EntityGraph(attributePaths = {
            "chapters",
            "chapters.lessons",
            "chapters.lessons.courseMaterials",
            "chapters.lessons.quizzes",
            "chapters.lessons.quizzes.questions",
            "chapters.lessons.quizzes.questions.mcpContents"
    })
    Optional<Course> findById(@Param("courseId") Long courseId);


    @Query(
           """
              SELECT new webtech.online.course.dtos.course.RecommendedCourseDTO(
                  c.id,
                  c.thumbnailUrl,
                  c.title,
                  AVG(rc.rating) as avgRating,
                  c.description,
                  COUNT(DISTINCT e) as numOfEnroll,
                  COUNT(DISTINCT ch),
                  c.isCompleted
              )
              FROM Course c
              LEFT JOIN c.tags t
              LEFT JOIN c.reviewCourses rc
              LEFT JOIN c.enrollments e
              LEFT JOIN c.chapters ch
              WHERE (:tagCount = 0L OR t.name IN :tags)
              GROUP BY c.id, c.thumbnailUrl, c.description, c.title, c.isCompleted, c.createdAt
              HAVING COALESCE(AVG(rc.rating), 0) >= :lowerBoundRating
                AND (:tagCount = 0 OR COUNT(DISTINCT t.id) = :tagCount)
        """
    )
    Page<RecommendedCourseDTO> getIntroduce(@Param("tags")List<String> tags, @Param("tagCount") Long tagCount, @Param("lowerBoundRating") Double lowerBoundRating, Pageable pageable);

    @Query("""
    SELECT t.name
    FROM Course c
    JOIN c.tags t
    GROUP BY t.id, t.name
    ORDER BY COUNT(c.id) DESC
    """)
    List<String> findPopularTag(Pageable pageable);
}
