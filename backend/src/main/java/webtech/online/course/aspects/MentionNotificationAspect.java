package webtech.online.course.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import webtech.online.course.annotations.MentionNotification;
import webtech.online.course.configs.FrontendConfig;
import webtech.online.course.dtos.post.CreateCommentDTO;
import webtech.online.course.dtos.post.CreatePostDTO;
import webtech.online.course.dtos.post.PostCommentDTO;
import webtech.online.course.dtos.post.PostDTO;
import webtech.online.course.models.Course;
import webtech.online.course.models.Enrollment;
import webtech.online.course.models.Post;
import webtech.online.course.repositories.CourseRepository;
import webtech.online.course.repositories.PostRepository;
import webtech.online.course.services.NotificationService;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MentionNotificationAspect {

    private final NotificationService notificationService;
    private final CourseRepository courseRepository;
    private final PostRepository postRepository;
    private final FrontendConfig frontendConfig;

    @AfterReturning(pointcut = "@annotation(mentionNotification)", returning = "result")
    public void handleMention(JoinPoint joinPoint, MentionNotification mentionNotification, Object result) {
        try {
            if (result instanceof PostDTO postDTO) {
                handlePostMention(joinPoint, postDTO);
            } else if (result instanceof PostCommentDTO commentDTO) {
                handleCommentMention(joinPoint, commentDTO);
            }
        } catch (Exception e) {
            log.error("Error processing mention notification", e);
        }
    }

    private void handlePostMention(JoinPoint joinPoint, PostDTO postDTO) {
        // Attempt to find CreatePostDTO arg to get courseId
        Object[] args = joinPoint.getArgs();
        Long courseId = null;
        for (Object arg : args) {
            if (arg instanceof CreatePostDTO dto) {
                courseId = dto.getCourseId();
                break;
            }
        }

        if (courseId != null && postDTO.getContent() != null && postDTO.getContent().contains("@All")) {
            notifyAllCourseUsers(courseId, "New Post in Course",
                    "User " + postDTO.getUserName() + " posted: " + truncate(postDTO.getTitle()),
                    "%s/course/%d/posts".formatted(frontendConfig.getUri(), courseId));
        }
    }

    private void handleCommentMention(JoinPoint joinPoint, PostCommentDTO commentDTO) {
        Object[] args = joinPoint.getArgs();
        Long postId = null;
        for (Object arg : args) {
            if (arg instanceof CreateCommentDTO dto) {
                postId = dto.getPostId();
                break;
            }
        }

        if (postId != null && commentDTO.getContent() != null && commentDTO.getContent().contains("@All")) {
            Post post = postRepository.findById(postId).orElse(null);
            if (post != null) {
                Long courseId = post.getCourse().getId();
                notifyAllCourseUsers(courseId, "New Comment in Course",
                        "User " + commentDTO.getUserName() + " commented: " + truncate(commentDTO.getContent()),
                        "/course/" + courseId + "/posts");
            }
        }
    }

    private void notifyAllCourseUsers(Long courseId, String title, String message, String url) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null)
            return;

        // Notify Instructor
        if (course.getInstructor() != null) {
            notificationService.createNotification(course.getInstructor().getId(), title, message, url);
        }

        // Notify Students
        List<Enrollment> enrollments = course.getEnrollments();
        if (enrollments != null) {
            for (Enrollment enrollment : enrollments) {
                notificationService.createNotification(enrollment.getUser().getId(), title, message, url);
            }
        }
    }

    private String truncate(String str) {
        if (str == null) return "";
        return str.length() > 50 ? str.substring(0, 47) + "..." : str;
    }
}
