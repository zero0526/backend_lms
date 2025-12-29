package webtech.online.course.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressId implements Serializable {

    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(name = "user_id")
    private Long userId;
}
