package webtech.online.course.models;


import jakarta.persistence.*;
import lombok.*;
import webtech.online.course.utils.Common;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"questions", "lesson"})
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    
    @OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Question> questions= new ArrayList<>();

    @OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<QuizAttempt> quizAttempts=  new ArrayList<>();

    private String title;

    private String precondition;

    private String description;

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;
    
    @Column(name = "difficulty_avg")
    private String difficultyAvg;
    
    @Column(name = "total_score")
    private Integer totalScore;

    public void addQuestion(Question question){
        this.questions.add(question);
        question.setQuiz(this);
    }

    public List<String> getDriverFilesId(){
        List<String> ids= new ArrayList<>();
        questions.forEach(q->ids.addAll(q.getDriverFilesId()));
        return ids;
    }
}


