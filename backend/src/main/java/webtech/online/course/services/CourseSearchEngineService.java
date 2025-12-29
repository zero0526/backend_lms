package webtech.online.course.services;

import webtech.online.course.document.CourseDocument;
import webtech.online.course.dtos.course.CourseSearchReq;

import java.time.LocalDateTime;
import java.util.List;

public interface CourseSearchEngineService {
    public List<Long> searchCourses(
            CourseSearchReq courseSearchReq
    );
    public void index(CourseDocument doc);
    public List<String> autoCompleteCourseTitle(String keyword, Integer limit);
}
