package tasks.productRecommendationEngineTask;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Recommendation {
    private final String productId;
    private double score;
    private String reason;
    private LocalDateTime generatedAt;

    public Recommendation(String productId, double score, String reason, LocalDateTime generatedAt) {
        this.productId = productId;
        this.score = score;
        this.reason = reason;
        this.generatedAt = generatedAt;
    }
}
