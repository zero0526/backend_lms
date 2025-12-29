package webtech.online.course.services;

import webtech.online.course.dtos.course.BaseChapterDTO;
import webtech.online.course.dtos.course.VideoProgressDTO;
import webtech.online.course.models.Course;
import webtech.online.course.models.Lesson;
import webtech.online.course.models.LessonProgress;
import webtech.online.course.models.User;

import java.util.List;

public interface LessonProgressService {
    public LessonProgress commit(VideoProgressDTO videoProgressDTO);

    public webtech.online.course.dtos.course.CourseOutlineResponseDTO getProgress(Long userId, Long courseId);
}
