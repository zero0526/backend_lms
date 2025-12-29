package webtech.online.course.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import webtech.online.course.dtos.course.ReviewCourseDTO;
import webtech.online.course.dtos.course.ReviewCourseRep;
import webtech.online.course.models.ReviewCourse;

import java.util.List;

public interface ReviewCourseService {
    public void saveReviewCourse(ReviewCourseDTO reviewCourseDTO);
    ReviewCourse findByUserIdAndCourseId(Long userId, Long courseId);
    public ReviewCourse save(ReviewCourseDTO reviewCourseDTO);
    Page<ReviewCourseRep> findByCourseId(Long id, Pageable pageable);
}
