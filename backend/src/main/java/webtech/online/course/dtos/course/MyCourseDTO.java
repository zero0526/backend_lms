package webtech.online.course.dtos.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyCourseDTO {
    private Long courseId;
    private String thumbnailUrl;
    private String title;
    private String description;
    private Double progress;
    private Double rating;
    private Long numOfEnroll;
    private Long numOfChapter;
    private String teacherName;
    private Boolean isCompleted;
}

