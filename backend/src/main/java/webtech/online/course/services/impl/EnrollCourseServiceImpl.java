package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.ResponseException;
import org.springframework.stereotype.Service;
import webtech.online.course.dtos.EnrollCourse.EnrollCourseDTO;
import webtech.online.course.dtos.course.BaseChapterDTO;
import webtech.online.course.dtos.course.BaseLessonDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.exceptions.ResourceNotFoundException;
import webtech.online.course.models.Course;
import webtech.online.course.models.Enrollment;
import webtech.online.course.models.EnrollmentId;
import webtech.online.course.models.User;
import webtech.online.course.repositories.CourseRepository;
import webtech.online.course.repositories.EnrollCourseRepository;
import webtech.online.course.repositories.UserRepository;
import webtech.online.course.services.CourseService;
import webtech.online.course.services.EnrollCourseService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollCourseServiceImpl implements EnrollCourseService {
    private final EnrollCourseRepository enrollCourseRepository;
    private final UserRepository userRepository;
    private final CourseService courseService;
    private final webtech.online.course.services.NotificationService notificationService;

    @Override
    @Transactional
    public void enrollment(EnrollCourseDTO enrollCourseDTO) {
        User user = userRepository.findById(enrollCourseDTO.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseService.findById(enrollCourseDTO.courseId());
        Enrollment enrollment = new Enrollment();

        EnrollmentId id = new EnrollmentId(enrollCourseDTO.userId(), enrollCourseDTO.courseId());
        enrollment.setId(id);

        enrollment.setUser(user);
        enrollment.setCourse(course);

        enrollment.setEnrolledAt(enrollCourseDTO.enrolledAt());
        enrollment.setProgressPercentage(0f);
        enrollCourseRepository.save(enrollment);

        // Notify Instructor
        if (course.getInstructor() != null) {
            notificationService.createNotification(
                    course.getInstructor().getId(),
                    "New Student Enrolled",
                    "Student " + user.getFullName() + " has enrolled in your course: " + course.getTitle(),
                    "null");
        }

        notificationService.createNotification(
                user.getId(),
                "Enrollment Successful",
                "You have successfully enrolled in: " + course.getTitle(),
                "/student/courses/" + course.getId());
    }

    public Enrollment findById(Long userId, Long courseId) {
        EnrollmentId enrollmentId = new EnrollmentId(userId, courseId);
        return enrollCourseRepository.findById(enrollmentId).orElse(null);
    }

}
