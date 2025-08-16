package tasks.ecommerceTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class OrderProcessingService {
    // Main method to test
    public OrderSummary processOrder(Order order) {
        // This method orchestrates all calculations and returns OrderSummary
        // You can implement this by calling the service methods or
        // implement the logic directly here
        if (order == null || order.items().isEmpty()) {
            throw new IllegalArgumentException("Order cannot be null or empty");
        }
        var subtotal = getScaledValue(getSubtotal(order));
        var totalDiscount = getScaledValue(getTotalDiscountsValue(order));
        var priceWithDiscount = getScaledValue(subtotal.subtract(totalDiscount));
        var shippingCost = calculateShippingCost(order, priceWithDiscount);
        var priceWithDiscountAndShippingCost = getScaledValue(priceWithDiscount.add(shippingCost));
        var taxAmount = getScaledValue(calculateTax(order, priceWithDiscountAndShippingCost));
        var finalTotal = getScaledValue(priceWithDiscountAndShippingCost.add(taxAmount));
        var volumeDiscount = getScaledValue(calculateVolumeDiscount(order, subtotal));
        var categoryDiscount = getScaledValue(calculateCategoryDiscount(order,
                subtotal.subtract(volumeDiscount)));
        var customerDiscount = getScaledValue(calculateCustomerTypeDiscount(order.customerType(),
                categoryDiscount));
        List<String> appliedDiscount = getAppliedDiscountDescriptions(volumeDiscount,
                categoryDiscount, customerDiscount);
        return new OrderSummary(subtotal, totalDiscount, shippingCost, taxAmount, finalTotal, appliedDiscount);
    }

    // Helper methods for discount calculations (implement these)
    public BigDecimal calculateVolumeDiscount(Order order, BigDecimal subtotal) {
        // Implement: 5% discount if total quantity >= 10 items
        var orderItemsSize = getTotalQuantity(order);
        if (orderItemsSize >= 10) {
            return getScaledValue(subtotal.multiply(BigDecimal.valueOf(0.05)));
        } else {
            return getScaledValue(BigDecimal.valueOf(0));
        }
    }

    public BigDecimal calculateCategoryDiscount(Order order, BigDecimal remainingAmount) {
        // Implement category-specific discounts:
        // ELECTRONICS: 3% if subtotal > $500
        // CLOTHING: 10% if subtotal > $200
        // BOOKS: 15% always
        // FOOD: 0%
        List<OrderItem> itemsToProcess = getTotalQuantity(order) >= 10 ?
                getApplyVolumeDiscountToItems(order) : order.items();
        var discount = calculateCategorySubtotal(itemsToProcess, Category.FOOD, 0);
        discount = discount.add(calculateCategorySubtotal(itemsToProcess, Category.BOOKS, 0.15));

        if (remainingAmount.compareTo(BigDecimal.valueOf(200)) > 0) {
            discount = discount.add(calculateCategorySubtotal(itemsToProcess, Category.CLOTHING, 0.1));
        } else {
            discount = discount.add(calculateCategorySubtotal(itemsToProcess, Category.CLOTHING, 0));
        }
        if (remainingAmount.compareTo(BigDecimal.valueOf(500)) > 0) {
            discount = discount.add(calculateCategorySubtotal(itemsToProcess, Category.ELECTRONICS, 0.03));
        } else {
            discount = discount.add(calculateCategorySubtotal(itemsToProcess, Category.ELECTRONICS, 0));
        }
        return discount;
    }

    private List<OrderItem> getApplyVolumeDiscountToItems(Order order) {
        return order.items().stream()
                .map(orderItem -> new OrderItem(orderItem.productId(), orderItem.productName(),
                        getScaledValue(orderItem.price().multiply(BigDecimal.valueOf(0.95))), orderItem.quantity(),
                        orderItem.category())).toList();
    }

    public BigDecimal calculateCustomerTypeDiscount(CustomerType customerType, BigDecimal remainingAmount) {
        // Implement customer tier discounts:
        // REGULAR: 0%, PREMIUM: 2%, VIP: 5%
        if (customerType.equals(CustomerType.VIP)) {
            return getScaledValue(remainingAmount.multiply(BigDecimal.valueOf(0.05)));
        }
        if (customerType.equals(CustomerType.PREMIUM)) {
            return getScaledValue(remainingAmount.multiply(BigDecimal.valueOf(0.02)));
        }
        return getScaledValue(BigDecimal.valueOf(0));
    }

    public BigDecimal calculateShippingCost(Order order, BigDecimal finalOrderAmount) {
        // Implement shipping logic:
        // Base: $10, International: +$25, Remote: +$15
        // Free if order total > $100
        var shippingCost = BigDecimal.valueOf(10);
        if (finalOrderAmount.compareTo(BigDecimal.valueOf(100)) > 0) {
            return getScaledValue(BigDecimal.valueOf(0));
        }
        if (isInternationalOrder(order.shippingAddress())) {
            shippingCost = shippingCost.add(BigDecimal.valueOf(25));
        }
        if (isRemoteAreaOrder(order.shippingAddress())) {
            shippingCost = shippingCost.add(BigDecimal.valueOf(15));
        }
        return getScaledValue(shippingCost);
    }

    public BigDecimal calculateTax(Order order, BigDecimal taxableAmount) {
        // Implement tax calculation:
        // USA: 8% tax, International: 0% tax
        // Taxable amount = subtotal - discounts + shipping
        if (order.shippingAddress().country().equals("USA")) {
            return getScaledValue(taxableAmount.multiply(BigDecimal.valueOf(0.08)));
        } else {
            return getScaledValue(BigDecimal.valueOf(0));
        }
    }

    public List<String> getAppliedDiscountDescriptions(BigDecimal volumeDiscount,
                                                       BigDecimal categoryDiscount,
                                                       BigDecimal customerDiscount) {
        // Return list of applied discount descriptions
        List<String> resultList = new ArrayList<>();
        resultList.add("Volume discount count " + volumeDiscount);
        resultList.add("Category discount count " + categoryDiscount);
        resultList.add("Customer type discount count " + customerDiscount);
        return resultList;
    }

    public boolean isInternationalOrder(ShippingAddress address) {
        // Check if country is not "USA"
        return !address.country().equals("USA");
    }

    public int getTotalQuantity(Order order) {
        // Calculate total quantity of all items
        return order.items().stream().map(OrderItem::quantity).reduce(Integer::sum).get();
    }

    public BigDecimal getSubtotal(Order order) {
        // Calculate subtotal (price * quantity for all items)
        return getScaledValue(order.items().stream().map(orderItem -> orderItem.price().multiply(
                BigDecimal.valueOf(orderItem.quantity()))).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private BigDecimal calculateCategorySubtotal(List<OrderItem> orderItemList, Category category,
                                                 double discountMultiplier) {
        return orderItemList.stream().filter(orderItem -> orderItem.category().equals(category))
                .map(orderItem -> orderItem.price().multiply(
                        BigDecimal.valueOf(orderItem.quantity())).multiply(BigDecimal.valueOf(discountMultiplier)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal getTotalDiscountsValue(Order order) {
        var subtotal = getSubtotal(order);
        var volumeDiscountValue = calculateVolumeDiscount(order, subtotal);
        var categoryDiscountValue = calculateCategoryDiscount(order, subtotal.subtract(volumeDiscountValue));
        var customerTypeDiscountValue = calculateCustomerTypeDiscount(
                order.customerType(), subtotal.subtract(volumeDiscountValue).subtract(categoryDiscountValue));
        return volumeDiscountValue.add(categoryDiscountValue).add(customerTypeDiscountValue);
    }

    private boolean isRemoteAreaOrder(ShippingAddress address) {
        return address.isRemoteArea();
    }

    private BigDecimal getScaledValue(BigDecimal valueToScale) {
        return valueToScale.setScale(2, RoundingMode.HALF_UP);
    }

    // Order creation helper methods for testing
    public Order createTestOrder(String orderId, String customerId,
                                 CustomerType customerType, ShippingAddress address,
                                 List<OrderItem> items) {
        return new Order(orderId, customerId, items, customerType, address);
    }

    public ShippingAddress createDomesticAddress(boolean isRemote) {
        return new ShippingAddress("USA", "California", isRemote);
    }

    public ShippingAddress createInternationalAddress(String country, boolean isRemote) {
        return new ShippingAddress(country, "Province", isRemote);
    }

    public OrderItem createOrderItem(String productId, String productName,
                                     String price, int quantity, Category category) {
        return new OrderItem(productId, productName, new BigDecimal(price), quantity, category);
    }
}
