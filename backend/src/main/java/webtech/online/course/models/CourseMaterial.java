package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import webtech.online.course.utils.Common;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_materials")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(name = "doc_url")
    private String docUrl;

    @Column(name = "title")
    private String title;

    @Column(name = "file_type")
    private String fileType;

    public List<String> getDriverFilesId(){
        List<String> ids= new ArrayList<>();
        if(docUrl!=null&& !docUrl.isEmpty()) ids.add(Common.extractDriveFileId(docUrl));
        return ids;
    }
}
