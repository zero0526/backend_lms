package webtech.online.course.dtos.course;


import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SearchedCourseRes {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private Long numOfEnroll;
    private String desc;
    private String thumbnailUrl;
    private Double rating;
    private String instructorName;
    private Boolean isCompleted;
}
