package webtech.online.course.dtos.course;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CourseUpdateDTO(
        Long instructorId,
        String title,
        String desc,
        MultipartFile thumbnail,
        String thumbnailUrl,
        List<String> courseTarget,
        List<String> precondition,
        List<String> tags,
        Boolean isCompleted
) {
}
