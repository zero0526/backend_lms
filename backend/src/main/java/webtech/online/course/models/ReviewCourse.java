package webtech.online.course.models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_courses")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ReviewCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "rating")
    private Integer rating;
    @Column(name = "comment", columnDefinition = "text")
    private String comment;
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt= LocalDateTime.now();
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt= LocalDateTime.now();
}

