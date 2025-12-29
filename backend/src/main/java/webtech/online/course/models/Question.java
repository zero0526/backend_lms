package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.stereotype.Component;
import webtech.online.course.utils.Common;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<MCPContent> mcpContents= new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(name = "question_text")
    private String questionText;

    @Column(name = "question_image")
    private String questionImg;

    private String level;

    private Float score;

    @Column(name = "\"order\"")
    private Integer order;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<QuestionComment> questionComments= new ArrayList<>();

    public void addMcpContent(MCPContent mcpContent){
        this.mcpContents.add(mcpContent);
        mcpContent.setQuestion(this);
    }
    public void addQuestionComment(QuestionComment questionComment){
        this.questionComments.add(questionComment);
        questionComment.setQuestion(this);
    }
    public List<String> getDriverFilesId(){
        List<String> ids= new ArrayList<>();
        mcpContents.forEach(mcp->ids.addAll(mcp.getFileId()));
        if(questionImg!=null&& !questionImg.isEmpty()) ids.add(Common.extractDriveFileId(questionImg));
        return ids;
    }
}
