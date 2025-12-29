package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import webtech.online.course.models.Course;
import webtech.online.course.models.Tag;
import webtech.online.course.repositories.TagRepository;
import webtech.online.course.services.TagService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public List<Tag> findOrCreateTags(List<String> tagNames) {
        List<Tag> existingTags = tagRepository.findByNameIn(tagNames);

        Set<String> existingNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        List<Tag> newTags = tagNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> {
                    Tag tag = Tag.builder().name(name).build();
                    return tagRepository.saveAndFlush(tag);
                })
                .toList();

        List<Tag> allTags = new ArrayList<>();
        allTags.addAll(existingTags);
        allTags.addAll(newTags);

        return allTags;
    }
}
