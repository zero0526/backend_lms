package webtech.online.course.dtos.course;

import org.springframework.web.multipart.MultipartFile;

public record McqContentDTO(
                Long id,
                String cText,
                MultipartFile cImage,
                Boolean isCorrect) {
}
