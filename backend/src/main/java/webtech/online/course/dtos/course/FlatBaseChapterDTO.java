package webtech.online.course.dtos.course;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class FlatBaseChapterDTO {
    Long chapterId;
    String titleChapter;
    Integer orderChapter;
    Long LessonId;
    String titleLesson;
    Integer orderLesson;
    Integer durationLesson;
    Double progress;
}
