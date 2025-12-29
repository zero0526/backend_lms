package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.*;
import webtech.online.course.utils.Common;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessons")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    private String title;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @OneToMany(mappedBy = "lesson",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CourseMaterial> courseMaterials= new ArrayList<>();

    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Quiz> quizzes= new ArrayList<>();

    @Column(name = "\"order\"")
    private Integer order;

    @Column(name = "description")
    private String description;

    @OneToOne(mappedBy = "lesson", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private LessonProgress lessonProgress;

    public void addQuiz(Quiz quiz){
        quizzes.add(quiz);
        quiz.setLesson(this);
    }
    public void addCourseMaterials(CourseMaterial courseMaterial){
        this.courseMaterials.add(courseMaterial);
        courseMaterial.setLesson(this);
    }
    public List<String> getDriverFilesId(){
        List<String> ids= new ArrayList<>();
        quizzes.forEach(q->ids.addAll(q.getDriverFilesId()));
        if(thumbnailUrl!=null&& !thumbnailUrl.isEmpty()) ids.add(Common.extractDriveFileId(thumbnailUrl));
        courseMaterials.forEach(cm->ids.addAll(cm.getDriverFilesId()));
        return ids;
    }
}
