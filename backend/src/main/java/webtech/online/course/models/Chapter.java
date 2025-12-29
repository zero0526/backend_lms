package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.*;
import webtech.online.course.utils.Common;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chapters")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"lessons", "course"})
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "title")
    private String title;

    @Column(name = "\"order\"")
    private Integer order;

    @OneToMany(mappedBy = "chapter", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Lesson> lessons= new ArrayList<>();

    public void addLesson(Lesson lesson){
        lessons.add(lesson);
        lesson.setChapter(this);
    }
    public List<String> getDriverFilesId(){
        List<String> ids= new ArrayList<>();
        lessons.forEach(l->ids.addAll(l.getDriverFilesId()));
        return ids;
    }
}