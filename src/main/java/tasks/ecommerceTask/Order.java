package tasks.ecommerceTask;

import java.util.List;

public record Order(String orderId, String customerId, List<OrderItem> items, CustomerType customerType,
                    ShippingAddress shippingAddress) {
}
