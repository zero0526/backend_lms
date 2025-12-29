package webtech.online.course.services;

import webtech.online.course.models.Course;
import webtech.online.course.models.Tag;

import java.util.List;

public interface TagService {
    public List<Tag> findOrCreateTags(List<String> tagNames);
}
