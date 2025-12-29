package webtech.online.course.services.impl;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CourseSuggestServiceTest {

    private CourseSuggestService courseSuggestService;
    private static final String TEST_FILE_NAME = "course_suggestions.bin";

    @BeforeEach
    void setUp() {
        // Clean up any existing file before each test
        File file = new File(TEST_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }

        courseSuggestService = new CourseSuggestService(new StandardAnalyzer());
        // Manually trigger init because we are not in a Spring container
        courseSuggestService.init();
    }

    @AfterEach
    void tearDown() {
        // Clean up after tests
        File file = new File(TEST_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void rebuildSuggestions_ShouldSaveToFileAndLoadInMemory() throws IOException {
        List<String> titles = List.of("Java Programming", "Advanced Java", "Spring Boot");

        courseSuggestService.rebuildSuggestions(titles);

        // Verify file exists
        File file = new File(TEST_FILE_NAME);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        // Verify in-memory suggestion works immediately
        List<String> results = courseSuggestService.suggest("Java", 5);
        assertEquals(2, results.size());
    }

    @Test
    void suggest_ShouldReturnSuggestions_WhenFileExistsAndLoaded() throws IOException {
        // 1. Create data and save to file
        List<String> titles = List.of("Java Programming", "Advanced Java", "Spring Boot");
        courseSuggestService.rebuildSuggestions(titles);

        // 2. Create a NEW service instance to simulate a restart
        CourseSuggestService newService = new CourseSuggestService(new StandardAnalyzer());
        newService.init(); // Should load from file

        // 3. Test suggest on new instance
        List<String> results = newService.suggest("Java", 5);

        assertEquals(2, results.size());
        assertTrue(results.contains("Java Programming"));
        assertTrue(results.contains("Advanced Java"));
    }

    @Test
    void suggest_ShouldReturnEmpty_WhenNoData() {
        // Service initialized with no file (deleted in setUp)
        List<String> results = courseSuggestService.suggest("Java", 5);

        assertTrue(results.isEmpty());
    }
}
