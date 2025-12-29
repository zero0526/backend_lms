package webtech.online.course.services;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import webtech.online.course.dtos.EnrollCourse.CourseStatistic;
import webtech.online.course.dtos.course.*;
import webtech.online.course.models.Chapter;
import webtech.online.course.models.Course;
import webtech.online.course.models.User;

import java.io.IOException;
import java.util.List;

public interface CourseService {
    public Course save(Course course, CourseDTO courseDTO) throws IOException;

    public Course findById(Long id);

    public Chapter addNewChapter(Long courseId, Chapter chapter);

    public Course update(Long id, CourseUpdateDTO courseDTO) throws IOException;

    public void delete(Long id);
//    public CourseDetailsDTO details(Long courseId);
    public List<RecommendedCourseDTO> recommendCourse(Long userId, Integer limit);
    public List<MyCourseDTO> getMyCourse(Long userId);
    public List<String> getAllTitlesFromDB();
    public List<SearchedCourseRes> findSearchedCourse(CourseSearchReq courseSearchReq);
    public DetailsCourseDTO getDetailsCourse(Long courseId);
    public  List<MyDevCourseDTO> getMyDevCourse(Long instructorId);
    public List<MyDevCourseDTO> getMyCompletedCourse(Long instructorId);
    public List<MyAllCourseDTO> getAllCreatedCourse(Long instructorId);
    public Page<RecommendedCourseDTO> getIntroduceCourse(List<String> tags, Double lowerBoundRating,String sortBy, int limit, int page);
    public List<String> getPopularTags(int limit);
}
