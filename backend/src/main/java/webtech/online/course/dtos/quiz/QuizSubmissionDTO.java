package webtech.online.course.dtos.quiz;

import java.util.List;

public record QuizSubmissionDTO(
        Long attemptId,
        List<AnswerSubmissionDTO> answers) {
}
