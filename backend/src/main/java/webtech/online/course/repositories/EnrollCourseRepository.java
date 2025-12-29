package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.Enrollment;
import webtech.online.course.models.EnrollmentId;

import java.util.List;
import java.util.Map;

@Repository
public interface EnrollCourseRepository extends JpaRepository<Enrollment, EnrollmentId> {
    @Query("""
        SELECT e2.course.id AS relatedCourseId, COUNT(e2) AS coCount
        FROM Enrollment e1
        JOIN Enrollment e2 
            ON e1.user.id = e2.user.id
            AND e2.course.id <> e1.course.id
        WHERE e1.course.id IN :likedCourseIds
          AND e2.course.id NOT IN (
               SELECT e3.course.id FROM Enrollment e3 WHERE e3.user.id = :userId
          )
        GROUP BY e2.course.id
        ORDER BY coCount DESC
    """)
    List<Map<String,Object>> findCoEnroll(@Param("likedCourseIds") List<Long> likedCourseIds,
                                          @Param("userId") Long userId);

}
