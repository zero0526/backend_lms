package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import webtech.online.course.document.CourseDocument;
import webtech.online.course.domains.FileInfo;
import webtech.online.course.dtos.Drive.DriveRequest;
import webtech.online.course.dtos.EnrollCourse.CourseStatistic;
import webtech.online.course.dtos.course.*;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.*;
import webtech.online.course.repositories.CourseRepository;
import webtech.online.course.services.*;
import webtech.online.course.utils.Common;
import webtech.online.course.utils.DatetimeUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    public final CourseRepository courseRepository;
    public final UserService userService;
    private final DriveService driveService;
    private final VideoService videoService;
    private final RecommendationService recommendationService;
    private final TagService tagService;
    private final CourseSearchEngineService courseSearchEngineService;

    @Override
    @Transactional
    public Course save(Course course, CourseDTO courseDTO) throws IOException {
        Long instructorId= courseDTO.instructorId();
        List<String> tags= courseDTO.tags().stream().map(String::toLowerCase).toList();
        String urlUploaded = "";
        if(courseDTO.thumbnailUrl()!=null){
            urlUploaded= courseDTO.thumbnailUrl();
        }
        else if (courseDTO.thumbnail()!=null && !courseDTO.thumbnail().isEmpty()) {
            FileInfo fileInfo = driveService.uploadFile(new DriveRequest(courseDTO.thumbnail()));
            urlUploaded = fileInfo.urlUploaded();
            log.debug("upload success {}" , urlUploaded);
        }
        course.setThumbnailUrl(urlUploaded);
        User instructor = userService.findById(instructorId);
        course.setInstructor(instructor);
        List<Tag> newTags= tagService.findOrCreateTags(tags);
        newTags.forEach(course::addTag);
        Course c = courseRepository.saveAndFlush(course);
        courseSearchEngineService.index(CourseDocument.builder()
                        .id(c.getId().toString())
                        .title(c.getTitle().toLowerCase())
                        .teacherName(instructor.getFullName().toLowerCase())
                        .createdAt(DatetimeUtils.toInstant(c.getCreatedAt()))
                        .tags(c.getTags().stream().map(Tag::getName).toList())
                .build());
        return c;
    }

    @Override
    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new BaseError(404, "not found cource has id= %d".formatted(id)));
    }

    @Override
    @Transactional
    public Chapter addNewChapter(Long courseId, Chapter chapter) {
        Course course = findById(courseId);
        course.addChapter(chapter);

        return chapter;
    }

    @Override
    @Transactional
    public Course update(Long id, CourseUpdateDTO courseDTO) throws IOException {
        Course course = findById(id);

        if (courseDTO.title() != null) {
            course.setTitle(courseDTO.title());
        }

        if (courseDTO.desc() != null) {
            course.setDescription(courseDTO.desc());
        }

        if (courseDTO.courseTarget() != null && !courseDTO.courseTarget().isEmpty()) {
            course.setCourseTarget(String.join("||", courseDTO.courseTarget()));
        }

        if (courseDTO.precondition() != null && !courseDTO.precondition().isEmpty()) {
            course.setPrecondition(String.join("||", courseDTO.precondition()));
        }

        if (courseDTO.thumbnail() != null && !courseDTO.thumbnail().isEmpty()) {
            FileInfo fileInfo = driveService.uploadFile(new DriveRequest(courseDTO.thumbnail()));
            course.setThumbnailUrl(fileInfo.urlUploaded());
        }

        if (courseDTO.instructorId() != null) {
            User instructor = userService.findById(courseDTO.instructorId());
            course.setInstructor(instructor);
        }
        if(courseDTO.isCompleted()!=null){
            course.setIsCompleted(courseDTO.isCompleted());
        }
        if (courseDTO.tags() != null && !courseDTO.tags().isEmpty()) {
            List<Tag> tagsEntities = tagService.findOrCreateTags(courseDTO.tags());

            course.getTags().removeIf(tag -> !tagsEntities.contains(tag));

            for (Tag tag : tagsEntities) {
                if (!course.getTags().contains(tag)) {
                    course.addTag(tag);
                }
            }
        }

        return courseRepository.saveAndFlush(course);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Course course= courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));;
        List<String> ids= course.getDriverFilesId();
        List<String> videoIds= new ArrayList<>();

        course.getChapters().forEach(ch->ch.getLessons().stream().filter(l->(l.getVideo()!=null&&!l.getVideo().getVideoUrl().isEmpty())).forEach(l->videoIds.add(Common.extractVideoId(l.getVideo().getVideoUrl()))));
        courseRepository.delete(course);
        driveService.deleteFiles(ids);
        videoService.deleteAllByVideoIds(videoIds);
    }

    @Transactional
    public RecommendedCourseDTO parseId2Recommend(RecommendationResponse recom){
        return courseRepository.findRecommendCourse(recom.courseId());
    }

    public List<RecommendedCourseDTO> recommendCourse(Long userId, Integer limit){
        List<RecommendationResponse> rrs = recommendationService.recommend(userId, limit);
        return rrs.stream().map(this::parseId2Recommend).toList();
    }

    @Override
    public List<MyCourseDTO> getMyCourse(Long userId) {
        return courseRepository.findMyCourse(userId);
    }

    @Override
    public List<String> getAllTitlesFromDB() {
        return courseRepository.getAllCourseTitles();
    }

    @Override
    public List<SearchedCourseRes> findSearchedCourse(CourseSearchReq courseSearchReq) {
        List<Long> ids= courseSearchEngineService.searchCourses(courseSearchReq);
        return courseRepository.findSearchCourseByIds(ids);
    }

    @Override
    public DetailsCourseDTO getDetailsCourse(Long courseId) {
        return courseRepository.findDetailsCourse(courseId);
    }

    @Override
    public List<MyDevCourseDTO> getMyDevCourse(Long instructorId) {
        return courseRepository.findMyDevCourse(instructorId, Boolean.FALSE);
    }

    @Override
    public List<MyDevCourseDTO> getMyCompletedCourse(Long instructorId) {
        return courseRepository.findMyDevCourse(instructorId, Boolean.TRUE);
    }
    @Override
    public List<MyAllCourseDTO> getAllCreatedCourse(Long instructorId) {
        return courseRepository.getAllCreatedCourse(instructorId);
    }

    @Override
    public Page<RecommendedCourseDTO> getIntroduceCourse(List<String> tags, Double lowerBoundRating, String sortBy, int limit, int page) {
        String sortField = (sortBy == null) ? "default" : sortBy.toLowerCase();

        Sort sort = switch (sortField) {
            case "popular" -> Sort.by("numOfEnroll").descending();
            case "latest"  -> Sort.by("createdAt").descending();
            default        -> Sort.by("avgRating").descending();
        };

        Pageable pageable = PageRequest.of(page, limit, sort);

        Long tagCount = (tags != null) ? (long) tags.size() : 0L;
        if(tags == null)tags= List.of();
        return courseRepository.getIntroduce(tags, tagCount, lowerBoundRating, pageable);
    }

    @Override
    public List<String> getPopularTags(int limit) {
        Pageable pageable= PageRequest.of(0, limit);
        return courseRepository.findPopularTag(pageable);
    }
}
