package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webtech.online.course.dtos.course.CommentQuestionDTO;
import webtech.online.course.dtos.quiz.QuestionCommentDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.Question;
import webtech.online.course.models.QuestionComment;
import webtech.online.course.models.User;
import webtech.online.course.repositories.QuestionCommentRepository;
import webtech.online.course.repositories.QuestionRepository;
import webtech.online.course.repositories.UserRepository;
import webtech.online.course.services.NotificationService;
import webtech.online.course.services.QuestionCommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionCommentServiceImpl implements QuestionCommentService {
    private final QuestionCommentRepository questionCommentRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public QuestionComment addComment(QuestionCommentDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseError(404, "User not found with id=" + userId));

        Question question = questionRepository.findById(dto.questionId())
                .orElseThrow(() -> new BaseError(404, "Question not found with id=" + dto.questionId()));

        QuestionComment.QuestionCommentBuilder builder = QuestionComment.builder()
                .question(question)
                .user(user)
                .content(dto.content())
                .isEdited(false);

        if (dto.parentCommentId() != null) {
            QuestionComment parentComment = questionCommentRepository.findById(dto.parentCommentId())
                    .orElseThrow(() -> new BaseError(404, "Parent comment not found with id=" + dto.parentCommentId()));
            builder.questionComment(parentComment);

            if (!parentComment.getUser().getId().equals(userId)) {
                notificationService.createNotification(
                        parentComment.getUser().getId(),
                        "New Reply",
                        user.getFullName() + " replied to your comment",
                        "/question/" + dto.questionId() + "/comment/" + dto.parentCommentId());
            }
        } else {

            List<QuestionComment> existingComments = questionCommentRepository.findByQuestionId(dto.questionId());
            Set<Long> userIdsToNotify = existingComments.stream()
                    .map(c -> c.getUser().getId())
                    .filter(id -> !id.equals(userId))
                    .collect(Collectors.toSet());

            for (Long userIdToNotify : userIdsToNotify) {
                notificationService.createNotification(
                        userIdToNotify,
                        "New Comment on Question",
                        user.getFullName() + " commented on a question you're following",
                        "/question/" + dto.questionId());
            }
        }

        QuestionComment comment = builder.build();
        return questionCommentRepository.save(comment);
    }

    @Override
    @Transactional
    public List<CommentQuestionDTO> getQuestionComments(Long questionId) {
        List<Object[]> results=  questionCommentRepository.findTopLevelCommentsWithChildCountByQuestionId(questionId);
        return results.stream().map(CommentQuestionDTO::parser).toList();
    }

    @Override
    @Transactional
    public QuestionComment updateComment(Long commentId, String content, Long userId) {
        QuestionComment comment = questionCommentRepository.findById(commentId)
                .orElseThrow(() -> new BaseError(404, "Comment not found with id=" + commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new BaseError(403, "You can only edit your own comments");
        }

        comment.setContent(content);
        comment.setIsEdited(true);
        comment.setUpdatedAt(LocalDateTime.now());

        return questionCommentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        QuestionComment comment = questionCommentRepository.findById(commentId)
                .orElseThrow(() -> new BaseError(404, "Comment not found with id=" + commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new BaseError(403, "You can only delete your own comments");
        }

        questionCommentRepository.delete(comment);
    }

    @Override
    public List<CommentQuestionDTO> getQuestionComment(Long questionCommentId) {
        return questionCommentRepository.getQuestionComment(questionCommentId);
    }
}
