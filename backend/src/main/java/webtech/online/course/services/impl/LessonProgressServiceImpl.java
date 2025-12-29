package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import webtech.online.course.dtos.course.BaseChapterDTO;
import webtech.online.course.dtos.course.BaseLessonDTO;
import webtech.online.course.dtos.course.FlatBaseChapterDTO;
import webtech.online.course.dtos.course.VideoProgressDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.*;
import webtech.online.course.repositories.LessonProgressRepository;
import webtech.online.course.services.CourseService;
import webtech.online.course.services.LessonProgressService;
import webtech.online.course.services.LessonService;
import webtech.online.course.services.LessonService;
import webtech.online.course.services.UserService;
import webtech.online.course.repositories.EnrollCourseRepository;
import webtech.online.course.models.EnrollmentId;
import webtech.online.course.dtos.course.CourseOutlineResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonProgressServiceImpl implements LessonProgressService {
    private final LessonProgressRepository lessonProgressRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final LessonService lessonService;
    private final EnrollCourseRepository enrollCourseRepository;

    public LessonProgress findByLessonIdAndUserId(Long userId, Long lessonId) {
        return lessonProgressRepository.findByIdLessonIdAndIdUserId(lessonId, userId).orElse(null);
    }

    @Override
    @Transactional
    public LessonProgress commit(VideoProgressDTO videoProgressDTO) {
        Long userId = videoProgressDTO.userId();
        Long courseId = videoProgressDTO.courseId();
        Long lessonId = videoProgressDTO.lessonId();
        LessonProgress lessonProgress = findByLessonIdAndUserId(userId, lessonId);
        User user = userService.findById(userId);
        Course course = courseService.findById(courseId);
        Lesson lesson = lessonService.findById(lessonId);
        if (course == null || user == null || lesson == null)
            throw new BaseError("Not found user %d, lesson %d, or course %d".formatted(userId, lessonId, courseId));
        Integer duration = lesson.getVideo().getDuration();
        float progress = videoProgressDTO.currentSecond() * 1.0f / duration;
        if (lessonProgress == null) {
            lessonProgress = new LessonProgress();
            lessonProgress.setId(new LessonProgressId(lesson.getId(), user.getId())); // CHỈ LÚC NÀY
            lessonProgress.setLesson(lesson);
            lessonProgress.setUser(user);
            lessonProgress.setLesson(lesson);
            lessonProgress.setProgressVideo(progress);
        }

        lessonProgress.setProgressVideo(Math.max(progress, lessonProgress.getProgressVideo()));
        return lessonProgressRepository.saveAndFlush(lessonProgress);
    }

    @Override
    public CourseOutlineResponseDTO getProgress(Long userId, Long courseId) {
        List<FlatBaseChapterDTO> flats = lessonProgressRepository.getProgress(userId, courseId);

        List<BaseChapterDTO> chapters = new ArrayList<>();

        if (!flats.isEmpty()) {
            BaseChapterDTO currentChapter = null;
            Long currentChapterId = null;

            for (FlatBaseChapterDTO flat : flats) {

                // Tạo chapter mới khi đổi chapterId
                if (!Objects.equals(currentChapterId, flat.getChapterId())) {
                    if (currentChapter != null) {
                        chapters.add(currentChapter);
                    }

                    currentChapter = new BaseChapterDTO();
                    currentChapter.setChapterId(flat.getChapterId());
                    currentChapter.setOrder(flat.getOrderChapter());
                    currentChapter.setTitle(flat.getTitleChapter());
                    currentChapter.setLessons(new ArrayList<>());

                    currentChapterId = flat.getChapterId();
                }

                // ⚠️ CHỈ add lesson khi lesson tồn tại
                if (flat.getLessonId() != null) {
                    currentChapter.getLessons().add(
                            new BaseLessonDTO(
                                    flat.getLessonId(),
                                    flat.getOrderLesson(),
                                    flat.getTitleLesson(),
                                    flat.getDurationLesson(),
                                    flat.getProgress()));
                }
            }

            if (currentChapter != null) {
                chapters.add(currentChapter);
            }
        }

        // Check enrollment
        boolean isEnrolled = false;
        if (userId != null) {
            isEnrolled = enrollCourseRepository.existsById(new EnrollmentId(userId, courseId));
        }

        return CourseOutlineResponseDTO.builder()
                .chapters(chapters)
                .isEnrolled(isEnrolled)
                .build();
    }

}
