package webtech.online.course.dtos.course;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCourseDTO {
    private Integer rating;
    private Long userid;
    private LocalDateTime at;
    private Long courseId;
    private String comment;
}
