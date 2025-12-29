package webtech.online.course.dtos.course;

import java.time.LocalDateTime;
import java.util.List;

public record CourseSearchReq(
        String title,
        List<String> tags,
        String instructorName,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        int page,
        int size
) {
}
