package tasks.productRecommendationEngineTask;

public record RecommendationConfig(int maxRecommendations, double minConfidence, int minSupportCount,
                                   double recencyWeight, double popularityWeight) {
}