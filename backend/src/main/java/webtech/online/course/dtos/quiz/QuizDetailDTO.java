package webtech.online.course.dtos.quiz;

import java.util.List;

public record QuizDetailDTO(
        Long id,
        String title,
        String precondition,
        String desc,
        Integer timeLimitMinutes,
        String difficultyAvg,
        Integer score,
        List<QuestionDetailDTO> questions) {
}
