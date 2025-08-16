package tasks.ecommerceTask;

import java.math.BigDecimal;
import java.util.List;

public record OrderSummary(BigDecimal subtotal, BigDecimal totalDiscount, BigDecimal shippingCost, BigDecimal taxAmount,
                           BigDecimal finalTotal, List<String> appliedDiscounts) {
}
