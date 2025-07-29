package tasks.productRecommendationEngineTask;

import java.time.LocalDateTime;

/**
 * @param segment "premium", "regular", "budget"
 */
public record Customer(String customerId, String segment, LocalDateTime registrationDate,
                       LocalDateTime lastActivityDate) {

}
