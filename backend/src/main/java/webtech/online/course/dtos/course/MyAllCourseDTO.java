package webtech.online.course.dtos.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyAllCourseDTO {
    private Long courseId;
    private String thumbnailUrl;
    private String title;
    private String description;
    private Double rating;
    private Long numOfEnroll;
    private Long numOfChapter;
    private Boolean isCompleted;
}
