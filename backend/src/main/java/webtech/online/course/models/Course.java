package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.*;
import webtech.online.course.utils.Common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Chapter> chapters;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewCourse> reviewCourses;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt= LocalDateTime.now();

    @Column(name = "last_updated")
    @Builder.Default
    private LocalDateTime updatedAt= LocalDateTime.now();

    @Column(name = "course_target")
    private String courseTarget;

    @Column(name = "precondition")
    private String precondition;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "course_tag",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();

    @Builder.Default
    @Column(name = "is_completed")
    private Boolean isCompleted=Boolean.FALSE;

    public void addTag(Tag tag) {
        if (tag.getCourses() == null) {
            tag.setCourses(new ArrayList<>());
        }
        tags.add(tag);
        tag.getCourses().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getCourses().remove(this);
    }
    public void addChapter(Chapter chapter){
        chapter.setCourse(this);
        this.chapters.add(chapter);
    }
    public List<String> getDriverFilesId(){
        List<String> ids= new ArrayList<>();
        chapters.forEach(c->ids.addAll(c.getDriverFilesId()));
        if(thumbnailUrl!=null&& !thumbnailUrl.isEmpty()) ids.add(Common.extractDriveFileId(thumbnailUrl));
        return ids;
    }
}