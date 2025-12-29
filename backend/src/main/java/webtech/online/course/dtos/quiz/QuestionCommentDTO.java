package webtech.online.course.dtos.quiz;

public record QuestionCommentDTO(
        Long questionId,
        Long parentCommentId,
        String content
) {
}
