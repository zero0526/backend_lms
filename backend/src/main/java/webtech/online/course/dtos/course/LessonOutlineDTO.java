package webtech.online.course.dtos.course;

public record LessonOutlineDTO(
        Long lessonId,
        String title,
        Long originalVideoDuration,
        Long lastWatchedAt
) {
}
