package tasks.ecommerceTask;

import java.math.BigDecimal;

public record OrderItem(String productId, String productName, BigDecimal price, Integer quantity, Category category) {
}
