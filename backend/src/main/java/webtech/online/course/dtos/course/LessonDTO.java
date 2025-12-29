package webtech.online.course.dtos.course;

import org.springframework.web.multipart.MultipartFile;
import webtech.online.course.dtos.video.VideoDTO;

import java.util.List;

public record LessonDTO(
        Long chapterId,
        String title,
        Integer order,
        String desc,
        VideoDTO videoDTO,
        List<CourseMaterialDTO> courseMaterialDTOs,
        MultipartFile thumbnail,
        List<QuizDTO> quizDTOs
) {
}
