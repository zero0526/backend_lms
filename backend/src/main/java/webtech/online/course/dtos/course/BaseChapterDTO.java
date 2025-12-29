package webtech.online.course.dtos.course;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseChapterDTO {
    Long chapterId;
    String title;
    Integer order;
    List<BaseLessonDTO> lessons;

}
