package webtech.online.course.services;

import webtech.online.course.dtos.course.CommentQuestionDTO;
import webtech.online.course.dtos.quiz.QuestionCommentDTO;
import webtech.online.course.models.QuestionComment;

import java.util.List;

public interface QuestionCommentService {
    QuestionComment addComment(QuestionCommentDTO dto, Long userId);

    List<CommentQuestionDTO> getQuestionComments(Long questionId);

    QuestionComment updateComment(Long commentId, String content, Long userId);

    void deleteComment(Long commentId, Long userId);

    List<CommentQuestionDTO> getQuestionComment(Long questionCommentId);
}
