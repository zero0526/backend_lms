package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "question_responses")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attempt_id")
    private QuizAttempt quizAttempt;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "is_selected")
    private Boolean isSelected;

    @Column(name = "score_awarded")
    private Float scoreAwarded;

    @Column(name = "answered_at")
    @Builder.Default
    private LocalDateTime answerAt=  LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "mcp_id")
    private MCPContent mcpContent;
}

