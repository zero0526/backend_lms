package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.Chapter;
import webtech.online.course.models.Lesson;
import webtech.online.course.repositories.ChapterRepository;
import webtech.online.course.services.ChapterService;
import webtech.online.course.services.DriveService;
import webtech.online.course.services.VideoService;
import webtech.online.course.utils.Common;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChapterServiceImpl implements ChapterService {
    private final ChapterRepository chapterRepository;
    private final webtech.online.course.services.CourseService courseService;
    private final DriveService driveService;
    private final VideoService videoService;
    @Override
    @Transactional
    public Lesson addLesson(Long chapterId, Lesson lesson) {
        Chapter chapter = findById(chapterId);
        chapter.addLesson(lesson);
        return lesson;
    }

    @Override
    public Chapter findById(Long chapterId) {
        return chapterRepository.findById(chapterId).orElseThrow(() -> new BaseError(404, "Not found exception"));
    }

    @Override
    @Transactional
    public Chapter save(webtech.online.course.dtos.course.ChapterDTO chapterDTO) {
        return courseService.addNewChapter(chapterDTO.courseId(), Chapter.builder()
                .title(chapterDTO.title())
                .order(chapterDTO.order())
                .build());
    }

    @Override
    public Chapter save(Chapter chapter) {
        return chapterRepository.save(chapter);
    }

    @Override
    @Transactional
    public Chapter update(Long id, webtech.online.course.dtos.course.ChapterDTO chapterDTO) {
        Chapter chapter = findById(id);
        chapter.setTitle(chapterDTO.title());
        chapter.setOrder(chapterDTO.order());
        return chapterRepository.saveAndFlush(chapter);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Chapter chapter = findById(id);
        List<String> ids= chapter.getDriverFilesId();
        List<String> videoIds= new ArrayList<>();
        chapter.getLessons().stream().filter(l->(l.getVideo()!=null&&!l.getVideo().getVideoUrl().isEmpty())).forEach(l->videoIds.add(Common.extractVideoId(l.getVideo().getVideoUrl())));
        chapterRepository.delete(chapter);
        driveService.deleteFiles(ids);
        videoService.deleteAllByVideoIds(videoIds);
    }

    @Override
    public java.util.List<Chapter> findAll() {
        return chapterRepository.findAll();
    }
}
