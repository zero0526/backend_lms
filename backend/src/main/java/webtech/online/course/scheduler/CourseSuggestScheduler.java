package webtech.online.course.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import webtech.online.course.services.CourseService;
import webtech.online.course.services.impl.CourseSuggestService;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CourseSuggestScheduler {

    private final CourseSuggestService suggestService;
    private final CourseService courseService;

    @Async
    @Scheduled(fixedRateString = "#{@schedulerConfig.courseSuggest}") // 1
    public void rebuild() throws IOException {
        log.info("Rebuilding course suggestions...");
        List<String> allTitles = courseService.getAllTitlesFromDB();
        suggestService.rebuildSuggestions(allTitles);
        log.info("Rebuild complete");
    }
}

