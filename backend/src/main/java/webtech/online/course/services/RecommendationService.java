package webtech.online.course.services;

import webtech.online.course.dtos.course.RecommendationResponse;

import java.util.List;

public interface RecommendationService {
    public List<RecommendationResponse> recommend(Long userId, int limit);
}
