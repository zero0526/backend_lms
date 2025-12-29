package webtech.online.course.dtos.course;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailsCourseDTO{
        Long courseId;
        String thumbnailUrl;
        Long numOfChapter;
        Long numOfLesson;
        Long courseDuration;
        Double rating;
        Long numOfRating;
        String title;
        String desc;
        String preconditions;
        String courseTargets;
        String teacherName;
        String teachAvatarUrl;
        Long instructorId;
        Boolean isCompleted;
}
