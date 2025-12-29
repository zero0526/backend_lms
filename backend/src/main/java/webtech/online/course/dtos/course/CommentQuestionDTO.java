package webtech.online.course.dtos.course;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class CommentQuestionDTO {
    private Long commentId;
    private Boolean isEdited;
    private LocalDateTime lastEdited;
    private Long userId;
    private String userName;
    private String avatar;
    private Long numOfChild;
    private String content;
    public CommentQuestionDTO(
            Long commentId,
            Boolean isEdited,
                              LocalDateTime lastEdited,
                              Long userId,
                              String userName,
                              String avatar,
                              Long numOfChild,
                              String content) {
        this.commentId= commentId;
        this.isEdited = isEdited;
        this.lastEdited = lastEdited;
        this.userId = userId;
        this.userName = userName;
        this.avatar = avatar;
        this.numOfChild = numOfChild;
        this.content = content;
    }

    public static CommentQuestionDTO parser(Object[] obj){
        return new CommentQuestionDTO(
                ((Number) obj[0]).longValue(),
                (Boolean) obj[1],
                ((Timestamp) obj[2]).toLocalDateTime(),
                ((Number) obj[3]).longValue(),
                (String) obj[4],
                (String) obj[5],
                ((Number) obj[6]).longValue(),
                (String) obj[7]
        );
    }
}
