package webtech.online.course.dtos.Lesson;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseLessonDTO {
    private Long lessonId;
    private String title;
    private Integer order;
    private Long duration;
    private Double progressLesson;

    private String urlVideo;
    private Integer lastWatchedAt;
    private String description;
    private List<BaseDocument> docs;
    private List<BaseQuizDTO> quizzes;
}
