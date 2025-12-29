package webtech.online.course.dtos.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String userName;
    private String userAvatar;
    private LocalDateTime createdAt;
    private Long commentsCount;
    private Boolean isPinned;
}
