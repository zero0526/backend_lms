package webtech.online.course.dtos.quiz;

public record McqDetailDTO(
        Long id,
        String cText,
        String cImage, // URL
        Boolean isCorrect) {
}
