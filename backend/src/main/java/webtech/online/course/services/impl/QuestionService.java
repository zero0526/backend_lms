package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webtech.online.course.domains.FileInfo;
import webtech.online.course.dtos.Drive.DriveRequest;
import webtech.online.course.dtos.course.CommentQuestionDTO;
import webtech.online.course.dtos.course.QuestionDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.Lesson;
import webtech.online.course.models.Question;
import webtech.online.course.repositories.QuestionRepository;
import webtech.online.course.services.DriveService;
import webtech.online.course.utils.Common;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final DriveService driveService;

    public Question findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new BaseError(500, "Not Found answer has id=%d".formatted(id)));
    }
    public List<CommentQuestionDTO> getQuestionComment(Long questionId) {
        return questionRepository.findCommentQuestion(questionId);
    }

    @Transactional
    public Question save(QuestionDTO questionDTO) throws IOException {
        String urlUploaded = "";
        if (!questionDTO.qImage().isEmpty()) {
            FileInfo fileInfo = driveService.uploadFile(new DriveRequest(questionDTO.qImage()));
            urlUploaded = fileInfo.urlUploaded();
        }
        return questionRepository.saveAndFlush(Question.builder()
                .questionImg(urlUploaded)
                .questionText(questionDTO.qText())
                .build());
    }

    @Transactional
    public Question update(Long id, QuestionDTO questionDTO) throws IOException {
        Question question = findById(id);
        question.setQuestionText(questionDTO.qText());
        if (questionDTO.qImage() != null && !questionDTO.qImage().isEmpty()) {
            FileInfo fileInfo = driveService.uploadFile(new DriveRequest(questionDTO.qImage()));
            question.setQuestionImg(fileInfo.urlUploaded());
        }
//        if(questionDTO.)
//        question.setExplanation();
        return questionRepository.saveAndFlush(question);
    }

    @Transactional
    public void delete(Long id) {
        Question question = questionRepository.findFullTree(id);
        List<String> ids= question.getDriverFilesId();
        questionRepository.delete(question);
        driveService.deleteFiles(ids);
    }

    public java.util.List<Question> findAll() {
        return questionRepository.findAll();
    }
}
