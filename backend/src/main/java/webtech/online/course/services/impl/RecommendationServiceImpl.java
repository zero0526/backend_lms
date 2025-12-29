package webtech.online.course.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webtech.online.course.dtos.course.PredictedCourse;
import webtech.online.course.dtos.course.RecommendationResponse;
import webtech.online.course.models.Course;
import webtech.online.course.repositories.CourseRepository;
import webtech.online.course.repositories.EnrollCourseRepository;
import webtech.online.course.repositories.ReviewCourseRepository;
import webtech.online.course.services.RecommendationService;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
        private final ReviewCourseRepository reviewRepo;
        private final EnrollCourseRepository enrollRepo;

        @Override
        public List<RecommendationResponse> recommend(Long userId, int limit) {
                Double mu = reviewRepo.findGlobalMeanRating();
                if (mu == null)
                        mu = 3.0;
                Double bUser = reviewRepo.findUserBias(userId, mu);
                if (bUser == null)
                        bUser = 0.0;

                List<Map<String, Object>> itemBiasRows = reviewRepo.findAllItemBias(mu, userId);

                Double finalBUser = bUser;
                Double finalMu = mu;
                List<PredictedCourse> predictions = itemBiasRows.stream()
                                .map(row -> {
                                        Long courseId = ((Number) row.get("courseId")).longValue();
                                        Double itemBias = row.get("itemBias") == null ? 0.0
                                                        : ((Number) row.get("itemBias")).doubleValue();

                                        double predict = finalMu + finalBUser + itemBias;

                                        return new PredictedCourse(courseId, predict);
                                })
                                .sorted(Comparator.comparingDouble(PredictedCourse::predictedRating).reversed())
                                .toList();

                // 5.predicted rating > expectation (μ + b_u)
                double userExpectedRating = mu + bUser;
                List<Long> likedCourseIds = predictions.stream()
                                .filter(p -> p.predictedRating() > userExpectedRating)
                                .map(PredictedCourse::courseId)
                                .toList();

                // fallback: ranking baseline
                if (likedCourseIds.isEmpty()) {
                        return predictions.stream()
                                        .limit(limit)
                                        .map(p -> new RecommendationResponse(p.courseId(), p.predictedRating()))
                                        .toList();
                }

                List<Map<String, Object>> coEnrollRows = enrollRepo.findCoEnroll(likedCourseIds, userId);

                return coEnrollRows.stream()
                                .limit(limit)
                                .map(row -> {
                                        Long id = ((Number) row.get("relatedCourseId")).longValue();
                                        Double score = ((Number) row.get("coCount")).doubleValue();
                                        return new RecommendationResponse(id, score);
                                })
                                .toList();
        }

}
