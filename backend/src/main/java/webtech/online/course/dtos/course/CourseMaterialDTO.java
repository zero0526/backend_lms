package webtech.online.course.dtos.course;

import org.springframework.web.multipart.MultipartFile;

public record CourseMaterialDTO(
        Long id,
        MultipartFile doc,
        String title
        )
{
}
