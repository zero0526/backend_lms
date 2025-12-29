package webtech.online.course.dtos.EnrollCourse;

import java.time.LocalDateTime;

public record EnrollCourseDTO(
        Long userId,
        Long courseId,
        LocalDateTime enrolledAt
) {
}
