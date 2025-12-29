package webtech.online.course.services;

import webtech.online.course.dtos.course.ChapterDTO;
import webtech.online.course.models.Chapter;
import webtech.online.course.models.Lesson;

public interface ChapterService {
    public Lesson addLesson(Long chapterId, Lesson lesson);

    public Chapter findById(Long chapterId);

    public Chapter save(ChapterDTO chapterDTO);
    public Chapter save(Chapter chapter);
    public Chapter update(Long id, ChapterDTO chapterDTO);

    public void delete(Long id);

    public java.util.List<Chapter> findAll();
}
