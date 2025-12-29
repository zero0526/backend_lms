package webtech.online.course.domains;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import webtech.online.course.models.Course;
import webtech.online.course.models.Tag;
import webtech.online.course.models.User;

public class CourseSpecific {
    public static Specification<Course> searchByRequest(CourseSearchDTO req){
        return (root, query, cb)->{
            Predicate predicates = cb.conjunction();
            if(req.getFrom()!=null){
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("createdAt"), req.getFrom()));
            }
            if(req.getTags()!=null && !req.getTags().isEmpty()){
                Join<Course, Tag> tagJoin = root.join("tags");
                predicates = cb.and(predicates, tagJoin.get("name").in(req.getTags()));
                assert query != null;
                query.distinct(true);
            }
            if(req.getKeyword()!=null){
                predicates= cb.and(predicates, cb.equal(root.get("title"), req.getKeyword()));
            }
            if(req.getInstructor()!=null){
                Join<Course, User> instructorJoin = root.join("instructor");
                predicates = cb.and(predicates, cb.like(cb.lower(instructorJoin.get("fullName")), "%" + req.getInstructor().toLowerCase() + "%"));
            }
            return predicates;
        };
    }
}
