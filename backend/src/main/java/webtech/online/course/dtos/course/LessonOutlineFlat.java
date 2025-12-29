package webtech.online.course.dtos.course;

public interface LessonOutlineFlat {
    String getChapterTitle();
    Long getLessonId();
    String getLessonTitle();
    Long getOriginalVideoDuration();
    Long getLastWatchedAt();
    Float getQuizProgress();
}
