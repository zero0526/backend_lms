package webtech.online.course.services.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;
import org.apache.lucene.store.*;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseSuggestService {
    private final Analyzer analyzer;

    // Cache in-memory
    private final AtomicReference<AnalyzingSuggester> currentSuggester = new AtomicReference<>();

    private static final String SUGGEST_FILE = "course_suggestions.bin";

    @PostConstruct
    public void init() {
        try {
            File file = new File(SUGGEST_FILE);
            if (file.exists()) {
                log.info("Loading suggestions from file: {}", file.getAbsolutePath());
                AnalyzingSuggester suggester = loadSuggesterFromFile(file);
                currentSuggester.set(suggester);
            } else {
                log.info("No suggestion file found at {}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to load suggestions from file", e);
        }
    }

    private AnalyzingSuggester loadSuggesterFromFile(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            DataInput di = new InputStreamDataInput(is);
            // Use ByteBuffersDirectory for in-memory operations (Lucene 8/9+)
            Directory directory = new ByteBuffersDirectory();
            AnalyzingSuggester suggester = new AnalyzingSuggester(directory, "COURSE_SUGGEST", analyzer);
            suggester.load(di);
            return suggester;
        }
    }

    public void rebuildSuggestions(List<String> titles) throws IOException {
        Directory directory = new ByteBuffersDirectory();
        AnalyzingSuggester suggester = new AnalyzingSuggester(directory, "COURSE_SUGGEST", analyzer);

        if (titles == null || titles.isEmpty()) {
            return;
        }

        InputIterator iterator = new InputIterator() {
            private final java.util.Iterator<String> wrapped = titles.iterator();

            @Override
            public BytesRef next() throws IOException {
                while (wrapped.hasNext()) {
                    String t = wrapped.next();
                    if (t != null && !t.isEmpty()) {
                        return new BytesRef(t);
                    }
                }
                return null;
            }

            @Override
            public long weight() {
                return 1L;
            }

            @Override
            public BytesRef payload() {
                return null;
            }

            @Override
            public boolean hasPayloads() {
                return false;
            }

            @Override
            public Set<BytesRef> contexts() {
                return null;
            }

            @Override
            public boolean hasContexts() {
                return false;
            }
        };

        suggester.build(iterator);

        // 1. Save to file
        saveSuggesterToFile(suggester);

        // 2. Update in-memory reference
        currentSuggester.set(suggester);
    }

    private void saveSuggesterToFile(AnalyzingSuggester suggester) throws IOException {
        try (OutputStream os = new FileOutputStream(SUGGEST_FILE)) {
            DataOutput out = new OutputStreamDataOutput(os);
            suggester.store(out);
        }
    }

    public List<String> suggest(String keyword, int limit) {
        AnalyzingSuggester suggester = currentSuggester.get();
        if (suggester == null)
            return List.of();

        try {
            List<Lookup.LookupResult> results = suggester.lookup(keyword, false, limit);
            return results.stream()
                    .map(r -> r.key.toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Error looking up suggestions", e);
            return List.of();
        }
    }
}