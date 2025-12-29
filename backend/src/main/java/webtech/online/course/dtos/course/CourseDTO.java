package webtech.online.course.dtos.course;

import jakarta.persistence.Column;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CourseDTO(
        Long instructorId,
        String title,
        String desc,
        MultipartFile thumbnail,
        String thumbnailUrl,
        List<String> courseTarget,
        List<String> precondition,
        List<String> tags
) {
}

