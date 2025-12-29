package webtech.online.course.dtos.Lesson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BaseDocument {
    Long id;
    String docUrl;
    String title;
    String fileType;
}
