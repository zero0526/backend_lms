package webtech.online.course.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "quiz_attempts")
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "quizAttempt", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<QuestionResponse> questionResponses= new ArrayList<>();
    @Column(name = "started_at")
    @Builder.Default
    private LocalDateTime startedAt= LocalDateTime.now();
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    @Column(name = "total_score")
    private Float totalScore;
    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted= Boolean.FALSE;

}