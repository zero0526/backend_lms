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
@Table(name = "mcq_contents")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MCPContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "choice_text", columnDefinition = "TEXT")
    private String choiceText;

    @Column(name = "choice_image")
    private String choiceImage;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @OneToMany(mappedBy = "mcpContent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<QuestionResponse> questionResponses= new ArrayList<>();

    public void addQuestionResponse(QuestionResponse  questionResponse){
        this.questionResponses.add(questionResponse);
        questionResponse.setMcpContent(this);
    }
    public List<String> getFileId(){
        if(choiceImage!=null&& !choiceImage.isEmpty()){
            return List.of(Common.extractDriveFileId(choiceImage));
        }
        return List.of();
    }
}
