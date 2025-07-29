package tasks.productRecommendationEngineTask;

import java.time.LocalDateTime;

public record Product(String productId, String name, String category, double price, boolean active,
                      LocalDateTime createdDate) {
}