package webtech.online.course.dtos.quiz;

import java.util.List;

public record AnswerSubmissionDTO(
        Long questionId,
        List<Long> selectedChoiceIds) {
}
