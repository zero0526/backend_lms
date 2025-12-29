package webtech.online.course.dtos.course;

import java.util.List;

public record CourseOutlineDTO(
        String chapterTitle,
        List<LessonOutlineDTO> lessonOutline
) {

}
