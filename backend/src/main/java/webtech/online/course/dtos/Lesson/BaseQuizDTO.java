package webtech.online.course.dtos.Lesson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BaseQuizDTO {
    Long quizId;
    String titleQuiz;
    String description;
    String numOfQuestion;
    String level;
    Integer timeLimit;
    Long attemptCount;
}
