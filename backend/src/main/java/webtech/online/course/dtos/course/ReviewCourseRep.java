package webtech.online.course.dtos.course;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCourseRep {
    private String fullName;
    private Integer rating;
    private String avatarUrl;
    private LocalDateTime lastUpdated;
    private String comment;
    private Boolean isEdit;
}
