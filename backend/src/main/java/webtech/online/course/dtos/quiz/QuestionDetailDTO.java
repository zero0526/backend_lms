package webtech.online.course.dtos.quiz;

import java.util.List;

public record QuestionDetailDTO(
        Long id,
        String qText,
        String qImage, // URL
        String explanation,
        String level,
        Float score,
        Integer order,
        List<McqDetailDTO> mcqContents) {
}
