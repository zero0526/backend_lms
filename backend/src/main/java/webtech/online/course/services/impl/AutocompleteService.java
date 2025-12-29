package webtech.online.course.services.impl;

import org.springframework.stereotype.Service;

@Service
public class AutocompleteService {
//    private final CourseSuggestService courseSuggestService;
//    private final StringRedisTemplate redis;
//
//    private static final Duration CACHE_TTL = Duration.ofMinutes(30);
//
//    public AutocompleteService(CourseSuggestService courseSuggestService,
//                               StringRedisTemplate redis) {
//        this.courseSuggestService = courseSuggestService;
//        this.redis = redis;
//    }
//
//    public List<String> autocomplete(String prefix) throws IOException {
//        String key = "suggest:" + prefix.toLowerCase().trim();
//        String cached = redis.opsForValue().get(key);
//        if (cached != null && !cached.isEmpty()) {
//            // assume cached is comma‑separated list (hoặc JSON)
//            return List.of(cached.split(","));  // hoặc parse JSON
//        }
//
//        List<String> suggestions = courseSuggestService.suggest(prefix, 10);
//        if (!suggestions.isEmpty()) {
//            String join = String.join(",", suggestions);
//            redis.opsForValue().set(key, join, CACHE_TTL);
//        }
//        return suggestions;
//    }
}
