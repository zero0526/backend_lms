package webtech.online.course.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import webtech.online.course.document.CourseDocument;
import webtech.online.course.dtos.course.CourseSearchReq;
import webtech.online.course.repositories.CourseESRepository;
import webtech.online.course.services.CourseSearchEngineService;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import webtech.online.course.services.CourseService;
import webtech.online.course.utils.DatetimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseEngineServiceImpl implements CourseSearchEngineService {
    private final ElasticsearchOperations elasticsearchOperations;
    private final CourseESRepository courseRepo;

    @Async
    public void index(CourseDocument doc) {
        courseRepo.save(doc);
    }

    @Override
    public List<Long> searchCourses(CourseSearchReq courseSearchReq) {
        Criteria criteria = new Criteria();

        // Title – fuzzy match
        if (courseSearchReq.title() != null && !courseSearchReq.title().isEmpty()) {
            criteria = criteria.and(new Criteria("title").fuzzy(courseSearchReq.title()));
        }

        // Tags – exact match
        if (courseSearchReq.tags() != null && !courseSearchReq.tags().isEmpty()) {
            criteria = criteria.and(new Criteria("tags").in(courseSearchReq.tags()));
        }

        // TeacherName – optional
        if (courseSearchReq.instructorName() != null && !courseSearchReq.instructorName().isEmpty()) {
            criteria = criteria.and(new Criteria("teacherName").fuzzy(courseSearchReq.instructorName()));
        }

        // CreatedAt – range filter
        if (courseSearchReq.fromDate() != null || courseSearchReq.toDate() != null) {
            Criteria dateCriteria = new Criteria("createdAt");
            if (courseSearchReq.fromDate() != null) dateCriteria = dateCriteria.greaterThanEqual(DatetimeUtils.toInstant(courseSearchReq.fromDate()));
            if (courseSearchReq.toDate() != null) dateCriteria = dateCriteria.lessThanEqual(DatetimeUtils.toInstant(courseSearchReq.toDate()));
            criteria = criteria.and(dateCriteria);
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setPageable(PageRequest.of(courseSearchReq.page(), courseSearchReq.size()));

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(query, CourseDocument.class);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .map(CourseDocument::getId)
                .map(Long::parseLong)
                .toList();
    }
    @Override
    public List<String> autoCompleteCourseTitle(String keyword, Integer limit) {

        if (keyword == null || keyword.isBlank()) return List.of();

        Criteria criteria = new Criteria("title").expression(keyword.toLowerCase() + "*");

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setPageable(PageRequest.of(0, limit));

        SearchHits<CourseDocument> hits = elasticsearchOperations.search(query, CourseDocument.class);

        return hits.stream()
                .map(hit -> hit.getContent().getTitle())
                .distinct()
                .toList();
    }
}
