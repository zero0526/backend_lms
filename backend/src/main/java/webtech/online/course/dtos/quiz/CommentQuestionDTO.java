package webtech.online.course.dtos.quiz;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentQuestionDTO {
    Long questionId;
    Long userId;
    Long parentCommentId;
    String content;
}
