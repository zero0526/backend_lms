package webtech.online.course.dtos.course;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import webtech.online.course.models.Lesson;

import java.util.List;

public record ChapterDTO(
        Long courseId,
        String title,
        Integer order
) {
}
