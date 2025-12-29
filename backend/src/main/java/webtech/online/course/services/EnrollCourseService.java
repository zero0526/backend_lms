package webtech.online.course.services;

import webtech.online.course.dtos.EnrollCourse.EnrollCourseDTO;
import webtech.online.course.dtos.course.BaseChapterDTO;
import webtech.online.course.dtos.course.ChapterDTO;
import webtech.online.course.models.Enrollment;

import java.util.List;

public interface EnrollCourseService {
    public void enrollment(EnrollCourseDTO enrollCourseDTO);
}
