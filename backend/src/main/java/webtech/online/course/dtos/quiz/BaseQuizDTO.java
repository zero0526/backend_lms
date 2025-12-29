package webtech.online.course.dtos.quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseQuizDTO {
    Long questionId;
    String questionText;
    String questionImg;
    String level;
    Float score;
    List<ChoiceNoAnswer> mcps;
}
