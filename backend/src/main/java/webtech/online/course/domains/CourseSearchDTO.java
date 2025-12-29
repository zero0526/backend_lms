package webtech.online.course.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseSearchDTO {
    private String keyword;
    private List<String> tags;
    private String instructor;
    private LocalDateTime from;
}
