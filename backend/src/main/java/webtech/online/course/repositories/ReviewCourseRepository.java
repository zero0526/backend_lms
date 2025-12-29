package webtech.online.course.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import webtech.online.course.dtos.course.ReviewCourseRep;
import webtech.online.course.models.ReviewCourse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ReviewCourseRepository extends JpaRepository<ReviewCourse, Long> {
    @Query("SELECT AVG(r.rating) FROM ReviewCourse r")
    Double findGlobalMeanRating();

    @Query("SELECT AVG(1.0 * r.rating - :globalMean) FROM ReviewCourse r WHERE r.user.id= :userId")
    Double findUserBias(@Param("userId") Long userId, @Param("globalMean") Double globalMean);

    @Query("""
                SELECT c.id AS courseId,
                       COALESCE(AVG(1.0 * r.rating - :globalMean), 0.0) AS itemBias
                FROM Course c
                LEFT JOIN ReviewCourse r ON r.course.id = c.id
                WHERE c.id NOT IN (
                    SELECT e.course.id FROM Enrollment e WHERE e.user.id = :userId
                )
                GROUP BY c.id
            """)
    List<Map<String, Object>> findAllItemBias(@Param("globalMean") Double globalMean,
            @Param("userId") Long userId);

    @Query("""
            SELECT rc FROM ReviewCourse rc WHERE rc.user.id=:userId AND rc.course.id=:courseId
            """)
    ReviewCourse findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("""
            SELECT new webtech.online.course.dtos.course.ReviewCourseRep(
                u.fullName,
                rc.rating,
                u.pictureUrl,
                rc.updatedAt,
                rc.comment,
                CASE WHEN rc.createdAt <> rc.updatedAt THEN true ELSE false END
            )
            FROM ReviewCourse rc
            JOIN rc.user u
            WHERE rc.course.id=:courseId
            ORDER BY rc.createdAt DESC
            """)
    Page<ReviewCourseRep> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);
}
