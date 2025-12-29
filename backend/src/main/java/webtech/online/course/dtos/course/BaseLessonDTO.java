package webtech.online.course.dtos.course;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseLessonDTO {
    Long lessonId;
    Integer order;
    String title;
    Integer duration;
    Double progressLesson;
}
