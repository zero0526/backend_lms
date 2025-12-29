package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import webtech.online.course.domains.FileInfo;
import webtech.online.course.dtos.Drive.DriveRequest;
import webtech.online.course.dtos.Drive.DriveResponse;
import webtech.online.course.dtos.Lesson.BaseDocument;
import webtech.online.course.dtos.Lesson.BaseLessonDTO;
import webtech.online.course.dtos.course.*;
import webtech.online.course.dtos.video.VideoDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.*;
import webtech.online.course.repositories.LessonRepository;
import webtech.online.course.services.*;
import webtech.online.course.utils.Common;
import webtech.online.course.utils.ObjectParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final QuizService quizService;
    private final VideoService videoService;
    private final CourseMaterialService courseMaterialService;
    private final DriveService driveService;

    @Override
    public Lesson insert(LessonDTO lessonDTO) throws IOException {

        Lesson lesson= Lesson.builder()
                .title(lessonDTO.title())
                .order(lessonDTO.order())
                .description(lessonDTO.desc())
                .build();

        if(lessonDTO.thumbnail()!=null && !lessonDTO.thumbnail().isEmpty()) {
            FileInfo resp = driveService.uploadFile(new DriveRequest(lessonDTO.thumbnail()));
            lesson.setThumbnailUrl(resp.urlUploaded());
        }

        if (lessonDTO.courseMaterialDTOs() != null && !lessonDTO.courseMaterialDTOs().isEmpty()) {
            lessonDTO.courseMaterialDTOs().forEach(cmDTO -> {
                CourseMaterial cm = courseMaterialService.parser(cmDTO);
                if(cm!=null){
                    cm.setTitle(cmDTO.title());
                    cm.setFileType(cm.getFileType());
                    lesson.addCourseMaterials(cm);
                }
            });
        }
        if (lessonDTO.videoDTO() != null && lessonDTO.videoDTO().video()!=null &&!lessonDTO.videoDTO().video().isEmpty()) {
            Video video = videoService.uploadVideo(lessonDTO.videoDTO());
            lesson.setVideo(video);
        }
        if (lessonDTO.quizDTOs() != null && !lessonDTO.quizDTOs().isEmpty()) {
            lessonDTO.quizDTOs().forEach(qz -> {
                Quiz quiz = quizService.uploadQuiz(qz);
                lesson.addQuiz(quiz);
            });
        }
        return lesson;
    }
    @Override
    public Lesson save(Lesson lesson){
        return lessonRepository.saveAndFlush(lesson);
    }
    @Transactional
    public Lesson update(Long id, LessonDTO lessonDTO) throws IOException {

        Lesson lesson = findById(id);

        // ===== PATCH Lesson fields =====
        if (lessonDTO.title() != null) {
            lesson.setTitle(lessonDTO.title());
        }

        if (lessonDTO.order() != null) {
            lesson.setOrder(lessonDTO.order());
        }

        if (lessonDTO.desc() != null) {
            lesson.setDescription(lessonDTO.desc());
        }

        // ===== PATCH Video =====
        if (lessonDTO.videoDTO() != null
                && lessonDTO.videoDTO().video() != null
                && !lessonDTO.videoDTO().video().isEmpty()) {

            Video oldVideo = lesson.getVideo();
            String oldVideoId = "";

            if (oldVideo != null && oldVideo.getVideoUrl() != null) {
                oldVideoId = Common.extractVideoId(oldVideo.getVideoUrl());
            }

            Video newVideo = videoService.uploadVideo(lessonDTO.videoDTO());
            lesson.setVideo(newVideo);

            if (!oldVideoId.isEmpty()) {
                videoService.deleteAllByVideoIds(List.of(oldVideoId));
            }
        }

        if (lessonDTO.thumbnail() != null && !lessonDTO.thumbnail().isEmpty()) {

            String oldThumbnailId = "";
            if (lesson.getThumbnailUrl() != null) {
                oldThumbnailId = Common.extractDriveFileId(lesson.getThumbnailUrl());
            }

            FileInfo resp = driveService.uploadFile(
                    new DriveRequest(lessonDTO.thumbnail()));
            lesson.setThumbnailUrl(resp.urlUploaded());

            if (!oldThumbnailId.isEmpty()) {
                driveService.deleteFiles(List.of(oldThumbnailId));
            }
        }

        if (lessonDTO.quizDTOs() != null) {
            for (QuizDTO quizDTO : lessonDTO.quizDTOs()) {
                if (quizDTO != null) {
                    if (quizDTO.id() != null) {
                        quizService.updateQuiz(quizDTO.id(), quizDTO);
                    } else {
                        addQuiz(lesson.getId(), quizDTO);
                    }
                }
            }
        }

        if (lessonDTO.courseMaterialDTOs() != null) {
            for (CourseMaterialDTO cmDTO : lessonDTO.courseMaterialDTOs()) {
                if (cmDTO != null) {
                    if (cmDTO.id() != null) {
                        courseMaterialService.update(cmDTO);
                    } else {
                        addCourseMaterial(lesson.getId(), cmDTO);
                    }
                }
            }
        }

        return lessonRepository.saveAndFlush(lesson);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(()->new BaseError("Not found the lesson has id=%d".formatted(id)));
        List<String> ids= lesson.getDriverFilesId();
        List<String> videoIds= new ArrayList<>();
        if(lesson.getVideo()!=null&&!lesson.getVideo().getVideoUrl().isEmpty()) videoIds.add(Common.extractVideoId(lesson.getVideo().getVideoUrl()));
        lessonRepository.delete(lesson);
        driveService.deleteFiles(ids);
        if(!videoIds.isEmpty()) videoService.deleteAllByVideoIds(videoIds);
    }

    @Override
    public List<Lesson> findAll() {
        return lessonRepository.findAll();
    }

    @Override
    public Lesson findById(Long id) {
        return lessonRepository.findById(id).orElseThrow(
                () -> new BaseError(404, "Lesson not found with id=" + id));
    }

    @Override
    @Transactional
    public void addQuiz(Long lessonId, QuizDTO quizDTO) {
        Lesson lesson = findById(lessonId);
        Quiz quiz = quizService.uploadQuiz(quizDTO);
        lesson.addQuiz(quiz);
        lessonRepository.saveAndFlush(lesson);
    }

    @Override
    @Transactional
    public void addCourseMaterial(Long lessonId, CourseMaterialDTO courseMaterialDTO) {
        Lesson lesson = findById(lessonId);
        CourseMaterial cm = courseMaterialService.parser(courseMaterialDTO);
        lesson.addCourseMaterials(cm);
        lessonRepository.saveAndFlush(lesson);
    }
    @Transactional
    @Override
    public BaseLessonDTO getDetailsLesson(Long lessonId, Long userId){
        Object obj= lessonRepository.getDetailsLesson(lessonId, userId);
        return ObjectParser.parseJson(BaseLessonDTO.class, obj.toString());
    }
}
