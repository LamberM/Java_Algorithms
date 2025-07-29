package tasks.productRecommendationEngineTask;

import java.time.LocalDateTime;

public record Purchase(String customerId, String productId, LocalDateTime purchaseDate, int quantity, double amount) {
}