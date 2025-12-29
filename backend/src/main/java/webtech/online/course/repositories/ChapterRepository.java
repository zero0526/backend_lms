package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.Chapter;
import webtech.online.course.models.Course;

import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    @EntityGraph(attributePaths = {
            "lessons",
            "lessons.courseMaterials",
            "lessons.quizzes",
            "lessons.quizzes.questions",
            "lessons.quizzes.questions.mcpContents"
    })
    Optional<Chapter> findById(Long chapterId);
}
