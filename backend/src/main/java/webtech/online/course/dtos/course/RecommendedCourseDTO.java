package webtech.online.course.dtos.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedCourseDTO {
    private Long id;

    private String thumbnailUrl;
    private String title;

    private Double avgRating;

    private String description;

    private Long numUserEnrolled;

    private Long numChapters;
    private Boolean isCompleted;
}
