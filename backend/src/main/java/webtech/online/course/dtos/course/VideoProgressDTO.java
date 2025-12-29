package webtech.online.course.dtos.course;

public record VideoProgressDTO(
        Long lessonId,
        Long userId,
        Long currentSecond,
        Long courseId
) {

}
