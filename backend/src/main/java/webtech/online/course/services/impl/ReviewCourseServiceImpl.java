package webtech.online.course.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import webtech.online.course.dtos.course.ReviewCourseDTO;
import webtech.online.course.dtos.course.ReviewCourseRep;
import webtech.online.course.models.Course;
import webtech.online.course.models.ReviewCourse;
import webtech.online.course.models.User;
import webtech.online.course.repositories.ReviewCourseRepository;
import webtech.online.course.services.CourseService;
import webtech.online.course.services.ReviewCourseService;
import webtech.online.course.services.UserService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewCourseServiceImpl implements ReviewCourseService {
    private final ReviewCourseRepository reviewCourseRepository;
    private final CourseService courseService;
    private final UserService userService;
    private final webtech.online.course.services.NotificationService notificationService;

    @Override
    public void saveReviewCourse(ReviewCourseDTO reviewCourseDTO) {
        ReviewCourse rc = reviewCourseRepository.findByUserIdAndCourseId(reviewCourseDTO.getUserid(),
                reviewCourseDTO.getCourseId());
        if (rc != null) {
            rc.setComment(reviewCourseDTO.getComment());
            rc.setRating(reviewCourseDTO.getRating());
            rc.setUpdatedAt(reviewCourseDTO.getAt());
            reviewCourseRepository.save(rc);
        } else {
            save(reviewCourseDTO);
        }
    }

    @Override
    public ReviewCourse findByUserIdAndCourseId(Long userId, Long courseId) {
        return reviewCourseRepository.findByUserIdAndCourseId(userId, courseId);
    }

    @Override
    public ReviewCourse save(ReviewCourseDTO reviewCourseDTO) {
        Course course = courseService.findById(reviewCourseDTO.getCourseId());
        User user = userService.findById(reviewCourseDTO.getUserid());
        ReviewCourse rc = ReviewCourse.builder()
                .course(course)
                .user(user)
                .comment(reviewCourseDTO.getComment())
                .createdAt(reviewCourseDTO.getAt())
                .updatedAt(reviewCourseDTO.getAt())
                .rating(reviewCourseDTO.getRating())
                .build();
        ReviewCourse saved = reviewCourseRepository.saveAndFlush(rc);

        // Notify Instructor
        if (course.getInstructor() != null) {
            notificationService.createNotification(
                    course.getInstructor().getId(),
                    "New Course Review",
                    "A student left a " + rc.getRating() + "-star review on your course: " + course.getTitle(),
                    "null");
        }
        return saved;
    }

    @Override
    public Page<ReviewCourseRep> findByCourseId(Long id, Pageable pageable) {
        return reviewCourseRepository.findByCourseId(id, pageable);
    }
}
