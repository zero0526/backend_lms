package webtech.online.course.dtos.course;

import java.util.List;

public record QuizDTO(
        Long id,
        String title,
        String precondition,
        String desc,
        Integer timeLimitMinutes,
        String difficultyAvg,
        Integer score,
        List<QuestionDTO> questions
) {
}
