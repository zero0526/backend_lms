package webtech.online.course.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import webtech.online.course.document.CourseDocument;

@Repository
public interface CourseESRepository extends ElasticsearchRepository<CourseDocument, String> {
}
