package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import webtech.online.course.domains.FileInfo;
import webtech.online.course.dtos.Drive.DriveRequest;
import webtech.online.course.dtos.course.McqContentDTO;
import webtech.online.course.dtos.course.QuestionDTO;
import webtech.online.course.dtos.course.QuizDTO;
import webtech.online.course.dtos.quiz.BaseQuizDTO;
import webtech.online.course.dtos.quiz.QuizDetailDTO;
import webtech.online.course.dtos.quiz.QuestionDetailDTO;
import webtech.online.course.dtos.quiz.McqDetailDTO;
import webtech.online.course.dtos.quiz.ChoiceNoAnswer;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.*;
import webtech.online.course.repositories.QuizRepository;
import webtech.online.course.services.DriveService;
import webtech.online.course.services.QuizAttemptService;
import webtech.online.course.services.QuizService;
import webtech.online.course.services.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final DriveService driveService;
    private final QuizAttemptService quizAttemptService;
    private final QuestionService questionService;
    private final UserService userService;


    @Transactional
    public Quiz uploadQuiz(QuizDTO quizDTO) {
        if (quizDTO == null) return null;

        Quiz quiz = Quiz.builder()
                .title(quizDTO.title())
                .precondition(quizDTO.precondition())
                .description(quizDTO.desc())
                .timeLimitMinutes(quizDTO.timeLimitMinutes())
                .difficultyAvg(quizDTO.difficultyAvg())
                .build();

        int totalScore = 0;

        if (quizDTO.questions() != null) {
            for (QuestionDTO qDTO : quizDTO.questions()) {

                String questionImg = null;
                if (qDTO.qImage() != null && !qDTO.qImage().isEmpty()) {
                    try {
                        questionImg = driveService
                                .uploadFile(new DriveRequest(qDTO.qImage()))
                                .urlUploaded();
                    } catch (IOException e) {
                         log.warn("Upload question image failed", e);
                    }
                }

                Integer score = qDTO.score() != null ? qDTO.score().intValue() : 0;
                totalScore += score;

                Question question = Question.builder()
                        .questionText(qDTO.qText())
                        .questionImg(questionImg)
                        .level(qDTO.level())
                        .explanation(qDTO.explanation())
                        .score(score.floatValue())
                        .order(qDTO.order())
                        .build();

                if (qDTO.mcqContents() != null) {
                    for (McqContentDTO mcq : qDTO.mcqContents()) {

                        String choiceImg = null;
                        if (mcq.cImage() != null && !mcq.cImage().isEmpty()) {
                            try {
                                choiceImg = driveService
                                        .uploadFile(new DriveRequest(mcq.cImage()))
                                        .urlUploaded();
                            } catch (IOException e) {
                                 log.warn("Upload choice image failed", e);
                            }
                        }

                        MCPContent mcpContent = MCPContent.builder()
                                .isCorrect(Boolean.TRUE.equals(mcq.isCorrect()))
                                .choiceImage(choiceImg)
                                .choiceText(mcq.cText())
                                .build();

                        question.addMcpContent(mcpContent);
                    }
                }

                quiz.addQuestion(question);
            }
        }

        quiz.setTotalScore(totalScore);
        return quiz;
    }


    public Quiz findById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new BaseError("Not found quiz has id %d".formatted(quizId)));
    }

    @Override
    @Transactional
    public Map<String, Object> getQuiz(Long quizId, Long userId) {
        Quiz quiz = findById(quizId);

        List<BaseQuizDTO> questions = quiz.getQuestions().stream()
                .map(q -> new BaseQuizDTO(
                        q.getId(),
                        q.getQuestionText(),
                        q.getQuestionImg(),
                        q.getLevel(),
                        q.getScore(),
                        q.getMcpContents().stream()
                                .map(mcp -> new ChoiceNoAnswer(
                                        mcp.getChoiceText(),
                                        mcp.getChoiceImage(),
                                        mcp.getId()))
                                .toList()))
                .toList();
        QuizAttempt quizAttempt = quizAttemptService.startAttempt(quizId, userId);

        return Map.of(
                "questions", questions,
                "timeLimit", quiz.getTimeLimitMinutes(),
                "attemptId", quizAttempt.getId());
    }

    @Override
    @Transactional
    public QuizDetailDTO getQuizDetail(Long id) {
        Quiz quiz = findById(id);
        return new QuizDetailDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getPrecondition(),
                quiz.getDescription(),
                quiz.getTimeLimitMinutes(),
                quiz.getDifficultyAvg(),
                quiz.getTotalScore(),
                quiz.getQuestions().stream().map(q -> new QuestionDetailDTO(
                        q.getId(),
                        q.getQuestionText(),
                        q.getQuestionImg(),
                        q.getExplanation(),
                        q.getLevel(),
                        q.getScore(),
                        q.getOrder(),
                        q.getMcpContents().stream().map(mcp -> new McqDetailDTO(
                                mcp.getId(),
                                mcp.getChoiceText(),
                                mcp.getChoiceImage(),
                                mcp.getIsCorrect())).toList()))
                        .toList());
    }

    @Override
    @Transactional
    public Quiz updateQuiz(Long id, QuizDTO quizDTO) {

        Quiz quiz = findById(id);

        // ===== PATCH Quiz fields =====
        if (quizDTO.title() != null) {
            quiz.setTitle(quizDTO.title());
        }

        if (quizDTO.precondition() != null) {
            quiz.setPrecondition(quizDTO.precondition());
        }

        if (quizDTO.desc() != null) {
            quiz.setDescription(quizDTO.desc());
        }

        if (quizDTO.timeLimitMinutes() != null) {
            quiz.setTimeLimitMinutes(quizDTO.timeLimitMinutes());
        }

        if (quizDTO.difficultyAvg() != null) {
            quiz.setDifficultyAvg(quizDTO.difficultyAvg());
        }

        // ===== PATCH Questions (KHÔNG REMOVE) =====
        if (quizDTO.questions() != null) {

            for (QuestionDTO qDTO : quizDTO.questions()) {

                Question question;

                if (qDTO.id() != null) {
                    question = quiz.getQuestions().stream()
                            .filter(q -> q.getId().equals(qDTO.id()))
                            .findFirst()
                            .orElseThrow(() ->
                                    new BaseError("Question id " + qDTO.id() + " not found in quiz " + id));
                } else {
                    question = new Question();
                    quiz.addQuestion(question);
                }

                if (qDTO.qText() != null) {
                    question.setQuestionText(qDTO.qText());
                }

                if (qDTO.explanation() != null) {
                    question.setExplanation(qDTO.explanation());
                }

                if (qDTO.level() != null) {
                    question.setLevel(qDTO.level());
                }

                if (qDTO.score() != null) {
                    question.setScore(qDTO.score());
                }

                if (qDTO.order() != null) {
                    question.setOrder(qDTO.order());
                }

                if (qDTO.qImage() != null && !qDTO.qImage().isEmpty()
                        && !qDTO.qImage().equals(question.getQuestionImg())) {
                    try {
                        FileInfo fInfo = driveService.uploadFile(
                                new DriveRequest(qDTO.qImage()));
                        question.setQuestionImg(fInfo.urlUploaded());
                    } catch (IOException e) {
                        throw new RuntimeException("Upload question image failed", e);
                    }
                }

                // ===== PATCH MCPContent (KHÔNG REMOVE) =====
                if (qDTO.mcqContents() != null) {

                    for (McqContentDTO mDTO : qDTO.mcqContents()) {

                        MCPContent mcp;

                        if (mDTO.id() != null) {
                            mcp = question.getMcpContents().stream()
                                    .filter(m -> m.getId().equals(mDTO.id()))
                                    .findFirst()
                                    .orElseThrow(() ->
                                            new BaseError("MCPContent id " + mDTO.id() + " not found"));
                        } else {
                            mcp = new MCPContent();
                            question.addMcpContent(mcp);
                        }

                        if (mDTO.cText() != null) {
                            mcp.setChoiceText(mDTO.cText());
                        }

                        if (mDTO.isCorrect() != null) {
                            mcp.setIsCorrect(mDTO.isCorrect());
                        }

                        if (mDTO.cImage() != null && !mDTO.cImage().isEmpty()
                                && !mDTO.cImage().equals(mcp.getChoiceImage())) {
                            try {
                                FileInfo fInfo = driveService.uploadFile(
                                        new DriveRequest(mDTO.cImage()));
                                mcp.setChoiceImage(fInfo.urlUploaded());
                            } catch (IOException e) {
                                throw new RuntimeException("Upload MCQ image failed", e);
                            }
                        }
                    }
                }
            }
        }

        int totalScore = quiz.getQuestions().stream()
                .map(Question::getScore)
                .filter(Objects::nonNull)
                .map(Float::intValue)
                .mapToInt(Integer::intValue).sum();

        quiz.setTotalScore(totalScore);

        return quizRepository.save(quiz);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Quiz quiz = quizRepository.findFullTree(id);
        List<String> ids = quiz.getDriverFilesId();
        quizRepository.delete(quiz);
        driveService.deleteFiles(ids);
    }
}
