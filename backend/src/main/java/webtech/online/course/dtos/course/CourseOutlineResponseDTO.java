package webtech.online.course.dtos.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseOutlineResponseDTO {
    private List<BaseChapterDTO> chapters;
    private Boolean isEnrolled;
}
