package webtech.online.course.dtos.course;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttemptResponseRow {

    private Long attemptId;
    private Float totalScore;

    private Long questionId;
    private String questionText;
    private String questionImg;
    private Integer questionOrder;
    private String explanation;

    private Boolean isSelected;
    private Boolean isCorrect;
    private Float scoreAwarded;
    private String choiceText;
    private String choiceImage;
    private Long numOfComment;
}
