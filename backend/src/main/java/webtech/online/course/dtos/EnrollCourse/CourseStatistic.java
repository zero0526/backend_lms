package webtech.online.course.dtos.EnrollCourse;

public record CourseStatistic(
        String thumbnailUrl,
        Long numOfChapter,
        Long numOfLesson,
        Long courseDuration,
        Double rating,
        Long numOfRating
) {
}
