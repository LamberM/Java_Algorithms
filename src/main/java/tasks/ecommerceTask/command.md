# Java Task: E-commerce Order Processing System

## Business Context
You are developing an order processing system for an e-commerce platform. The system needs to calculate the final price of an order considering various business rules including discounts, shipping costs, and taxes.

## Task Requirements

### 1. Create an `Order` class
Create a class to represent an order with the following properties:
- `orderId` (String)
- `customerId` (String)
- `items` (List of OrderItem)
- `customerType` (enum: REGULAR, PREMIUM, VIP)
- `shippingAddress` (ShippingAddress object)

### 2. Create an `OrderItem` class
- `productId` (String)
- `productName` (String)
- `price` (BigDecimal)
- `quantity` (int)
- `category` (enum: ELECTRONICS, CLOTHING, BOOKS, FOOD)

### 3. Create a `ShippingAddress` class
- `country` (String)
- `state` (String)
- `isRemoteArea` (boolean)

### 6. Create `OrderCalculator` class (Helper/Utility Class)
This is a helper class that contains the core calculation logic:
```java
public class OrderCalculator {
    
    public OrderSummary calculateOrderTotal(Order order) {
        // This method orchestrates all calculations and returns OrderSummary
        // You can implement this by calling the service methods or 
        // implement the logic directly here
    }
}
```

**Architecture Options:**
You can choose one of these approaches:

**Option A: Calculator as main logic holder**
- `OrderCalculator` contains all the business logic
- `OrderProcessingService` just calls `OrderCalculator.calculateOrderTotal()`

**Option B: Service as main logic holder**
- `OrderProcessingService` contains all the individual calculation methods
- `OrderCalculator.calculateOrderTotal()` calls service methods to do the work

**Option C: Calculator as pure utility**
- Both service and calculator have calculation methods
- Calculator focuses on complex math, service handles workflow

## Business Logic Rules

### Discount Rules (applied in order):
1. **Volume Discount**: If total quantity of items ≥ 10, apply 5% discount on subtotal
2. **Category Discount**:
    - ELECTRONICS: 3% discount if subtotal > $500
    - CLOTHING: 10% discount if subtotal > $200
    - BOOKS: 15% discount (always applied)
    - FOOD: No discount
3. **Customer Type Discount**:
    - REGULAR: No additional discount
    - PREMIUM: 2% additional discount
    - VIP: 5% additional discount

### Shipping Cost Rules:
- Base shipping: $10
- International shipping (country != "USA"): +$25
- Remote area: +$15
- Free shipping if order total (after discounts) > $100

### Tax Rules:
- Domestic orders (USA): 8% tax on (subtotal - discounts + shipping)
- International orders: No tax

### 5. Create an `OrderSummary` class (Data Transfer Object)
This class holds the calculation results:
```java
public class OrderSummary {
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal shippingCost;
    private BigDecimal taxAmount;
    private BigDecimal finalTotal;
    private List<String> appliedDiscounts;
    
    // Constructor
    public OrderSummary(BigDecimal subtotal, BigDecimal totalDiscount, 
                       BigDecimal shippingCost, BigDecimal taxAmount, 
                       BigDecimal finalTotal, List<String> appliedDiscounts) {
        this.subtotal = subtotal;
        this.totalDiscount = totalDiscount;
        this.shippingCost = shippingCost;
        this.taxAmount = taxAmount;
        this.finalTotal = finalTotal;
        this.appliedDiscounts = new ArrayList<>(appliedDiscounts);
    }
    
    // Getters
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTotalDiscount() { return totalDiscount; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getFinalTotal() { return finalTotal; }
    public List<String> getAppliedDiscounts() { return new ArrayList<>(appliedDiscounts); }
    
    // Optional: toString, equals, hashCode methods
    @Override
    public String toString() {
        return String.format("OrderSummary{subtotal=%s, totalDiscount=%s, shippingCost=%s, taxAmount=%s, finalTotal=%s, appliedDiscounts=%s}",
                subtotal, totalDiscount, shippingCost, taxAmount, finalTotal, appliedDiscounts);
    }
}
```

## Test Cases

```java
package tasks.ecommerceTask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderProcessingServiceTest {
    private OrderProcessingService service;

    @BeforeEach
    void setUp() {
        service = new OrderProcessingService();
    }

    @Test
    void testProcessOrder_RegularCustomerDomestic() {
        // Given
        Order order = service.createTestOrder("REG001", "CUST001", CustomerType.REGULAR,
                service.createDomesticAddress(false),
                Arrays.asList(
                        service.createOrderItem("P001", "Laptop", "800", 1, Category.ELECTRONICS),
                        service.createOrderItem("P002", "Mouse", "25", 2, Category.ELECTRONICS)
                ));

        // When
        OrderSummary result = service.processOrder(order);

        // Then
        assertEquals(new BigDecimal("850.00"), result.subtotal());
        assertEquals(new BigDecimal("25.50"), result.totalDiscount());
        assertEquals(new BigDecimal("0.00"), result.shippingCost());
        assertEquals(new BigDecimal("65.96"), result.taxAmount());
        assertEquals(new BigDecimal("890.46"), result.finalTotal());
    }

    @Test
    void testProcessOrder_VipCustomerInternational() {
        // Given
        Order order = service.createTestOrder("VIP001", "CUST002", CustomerType.VIP,
                service.createInternationalAddress("Canada", true),
                Arrays.asList(
                        service.createOrderItem("P003", "T-Shirt", "30", 15, Category.CLOTHING)
                ));

        // When
        OrderSummary result = service.processOrder(order);

        // Then
        assertEquals(new BigDecimal("450.00"), result.subtotal());
        assertEquals(new BigDecimal("84.49"), result.totalDiscount());
        assertEquals(new BigDecimal("0.00"), result.shippingCost());
        assertEquals(new BigDecimal("0.00"), result.taxAmount());
        assertEquals(new BigDecimal("365.51"), result.finalTotal());
        assertEquals(3, result.appliedDiscounts().size());
    }

    @Test
    void testProcessOrder_BooksWithMultipleDiscounts() {
        // Given
        Order order = service.createTestOrder("BOOK001", "CUST003", CustomerType.PREMIUM,
                service.createDomesticAddress(false),
                Arrays.asList(
                        service.createOrderItem("P004", "Java Book", "45", 5, Category.BOOKS),
                        service.createOrderItem("P005", "Python Book", "40", 6, Category.BOOKS)
                ));

        // When
        OrderSummary result = service.processOrder(order);

        // Then
        assertEquals(new BigDecimal("465.00"), result.subtotal());
        assertEquals(new BigDecimal("97.02"), result.totalDiscount());
        assertEquals(new BigDecimal("0.00"), result.shippingCost());
        assertEquals(new BigDecimal("29.44"), result.taxAmount());
        assertEquals(new BigDecimal("397.42"), result.finalTotal());
    }

    @Test
    void testProcessOrder_SmallOrderWithShipping() {
        // Given
        Order order = service.createTestOrder("SMALL001", "CUST004", CustomerType.REGULAR,
                service.createDomesticAddress(false),
                Arrays.asList(
                        service.createOrderItem("P006", "Snack", "15", 2, Category.FOOD)
                ));

        // When
        OrderSummary result = service.processOrder(order);

        // Then
        assertEquals(new BigDecimal("30.00"), result.subtotal());
        assertEquals(new BigDecimal("0.00"), result.totalDiscount());
        assertEquals(new BigDecimal("10.00"), result.shippingCost());
        assertEquals(new BigDecimal("3.20"), result.taxAmount());
        assertEquals(new BigDecimal("43.20"), result.finalTotal());
    }

    // Unit tests for individual methods
    @Test
    void testCalculateVolumeDiscount_EligibleForDiscount() {
        // Given
        Order order = service.createTestOrder("TEST", "CUST", CustomerType.REGULAR,
                service.createDomesticAddress(false),
                Arrays.asList(service.createOrderItem("P1", "Item", "10", 12, Category.ELECTRONICS)));

        // When
        BigDecimal discount = service.calculateVolumeDiscount(order, new BigDecimal("120"));

        // Then
        assertEquals(new BigDecimal("6.00"), discount); // 5% of 120
    }

    @Test
    void testCalculateVolumeDiscount_NotEligible() {
        // Given
        Order order = service.createTestOrder("TEST", "CUST", CustomerType.REGULAR,
                service.createDomesticAddress(false),
                Arrays.asList(service.createOrderItem("P1", "Item", "10", 5, Category.ELECTRONICS)));

        // When
        BigDecimal discount = service.calculateVolumeDiscount(order, new BigDecimal("50"));

        // Then
        assertEquals(new BigDecimal("0.00"), discount);
    }

    @Test
    void testCalculateCategoryDiscount_Electronics() {
        // Given
        Order order = service.createTestOrder("TEST", "CUST", CustomerType.REGULAR,
                service.createDomesticAddress(false),
                Arrays.asList(service.createOrderItem("P1", "Laptop", "600", 1, Category.ELECTRONICS)));

        // When
        BigDecimal discount = service.calculateCategoryDiscount(order, new BigDecimal("600"));

        // Then
        assertEquals(new BigDecimal("18.00"), discount); // 3% of 600
    }

    @Test
    void testCalculateCategoryDiscount_Books() {
        // Given
        Order order = service.createTestOrder("TEST", "CUST", CustomerType.REGULAR,
                service.createDomesticAddress(false),
                Arrays.asList(service.createOrderItem("P1", "Book", "20", 1, Category.BOOKS)));

        // When
        BigDecimal discount = service.calculateCategoryDiscount(order, new BigDecimal("20"));

        // Then
        assertEquals(new BigDecimal("3.00"), discount); // 15% of 20
    }

    @Test
    void testCalculateCustomerTypeDiscount_VIP() {
        // When
        BigDecimal discount = service.calculateCustomerTypeDiscount(CustomerType.VIP, new BigDecimal("100"));

        // Then
        assertEquals(new BigDecimal("5.00"), discount); // 5% of 100
    }

    @Test
    void testCalculateShippingCost_International() {
        // Given
        Order order = service.createTestOrder("TEST", "CUST", CustomerType.REGULAR,
                service.createInternationalAddress("Canada", false),
                Arrays.asList(service.createOrderItem("P1", "Item", "10", 1, Category.FOOD)));

        // When
        BigDecimal shipping = service.calculateShippingCost(order, new BigDecimal("50"));

        // Then
        assertEquals(new BigDecimal("35.00"), shipping); // $10 base + $25 international
    }

    @Test
    void testCalculateShippingCost_FreeShipping() {
        // Given
        Order order = service.createTestOrder("TEST", "CUST", CustomerType.REGULAR,
                service.createDomesticAddress(false),
                Arrays.asList(service.createOrderItem("P1", "Item", "10", 1, Category.FOOD)));

        // When
        BigDecimal shipping = service.calculateShippingCost(order, new BigDecimal("150"));

        // Then
        assertEquals(new BigDecimal("0.00"), shipping); // Free shipping over $100
    }

    @Test
    void testCalculateTax_Domestic() {
        // Given
        Order order = service.createTestOrder("TEST", "CUST", CustomerType.REGULAR,
                service.createDomesticAddress(false),
                Arrays.asList(service.createOrderItem("P1", "Item", "10", 1, Category.FOOD)));

        // When
        BigDecimal tax = service.calculateTax(order, new BigDecimal("100"));

        // Then
        assertEquals(new BigDecimal("8.00"), tax); // 8% of 100
    }

    @Test
    void testCalculateTax_International() {
        // Given
        Order order = service.createTestOrder("TEST", "CUST", CustomerType.REGULAR,
                service.createInternationalAddress("Canada", false),
                Arrays.asList(service.createOrderItem("P1", "Item", "10", 1, Category.FOOD)));

        // When
        BigDecimal tax = service.calculateTax(order, new BigDecimal("100"));

        // Then
        assertEquals(new BigDecimal("0.00"), tax); // No tax for international orders
    }

    @Test
    void testIsInternationalOrder() {
        assertTrue(service.isInternationalOrder(service.createInternationalAddress("Canada", false)));
        assertFalse(service.isInternationalOrder(service.createDomesticAddress(false)));
    }

    @Test
    void testGetTotalQuantity() {
        // Given
        Order order = service.createTestOrder("TEST", "CUST", CustomerType.REGULAR,
                service.createDomesticAddress(false),
                Arrays.asList(
                        service.createOrderItem("P1", "Item1", "10", 3, Category.FOOD),
                        service.createOrderItem("P2", "Item2", "20", 5, Category.BOOKS)
                ));

        // When
        int totalQuantity = service.getTotalQuantity(order);

        // Then
        assertEquals(8, totalQuantity);
    }

    @Test
    void testsubtotal() {
        // Given
        Order order = service.createTestOrder("TEST", "CUST", CustomerType.REGULAR,
                service.createDomesticAddress(false),
                Arrays.asList(
                        service.createOrderItem("P1", "Item1", "10", 3, Category.FOOD),
                        service.createOrderItem("P2", "Item2", "20", 2, Category.BOOKS)
                ));

        // When
        BigDecimal subtotal = service.getSubtotal(order);

        // Then
        assertEquals(new BigDecimal("70.00"), subtotal); // (10*3) + (20*2)
    }
}
```

## Notes
- Use `BigDecimal` for all monetary calculations to avoid precision issues
- Round all monetary values to 2 decimal places using `RoundingMode.HALF_UP`
- Discounts should be calculated cumulatively (each discount applies to the remaining amount)
- Make sure to handle edge cases and validate inputs appropriately
- Include proper constructors, getters, and `equals`/`hashCode` methods where needed

## Complete Service Implementation for Testing

```java
public class OrderProcessingService {
    // Main method to test
    public OrderSummary processOrder(Order order) {
        // This method orchestrates all calculations and returns OrderSummary
        // You can implement this by calling the service methods or
        // implement the logic directly here
    }
    
    // Helper methods for discount calculations (implement these)
    public BigDecimal calculateVolumeDiscount(Order order, BigDecimal subtotal) {
        // Implement: 5% discount if total quantity >= 10 items
    }
    
    public BigDecimal calculateCategoryDiscount(Order order, BigDecimal remainingAmount) {
        // Implement category-specific discounts:
        // ELECTRONICS: 3% if subtotal > $500
        // CLOTHING: 10% if subtotal > $200  
        // BOOKS: 15% always
        // FOOD: 0%
    }
    
    public BigDecimal calculateCustomerTypeDiscount(CustomerType customerType, BigDecimal remainingAmount) {
        // Implement customer tier discounts:
        // REGULAR: 0%, PREMIUM: 2%, VIP: 5%
    }
    
    public BigDecimal calculateShippingCost(Order order, BigDecimal finalOrderAmount) {
        // Implement shipping logic:
        // Base: $10, International: +$25, Remote: +$15
        // Free if order total > $100
    }
    
    public BigDecimal calculateTax(Order order, BigDecimal taxableAmount) {
        // Implement tax calculation:
        // USA: 8% tax, International: 0% tax
        // Taxable amount = subtotal - discounts + shipping
    }
    
    public List<String> getAppliedDiscountDescriptions(Order order, 
                                                      BigDecimal volumeDiscount,
                                                      BigDecimal categoryDiscount, 
                                                      BigDecimal customerDiscount) {
        // Return list of applied discount descriptions
    }
    
    // Validation methods (implement these)
    public boolean isValidOrder(Order order) {
        // Validate order has items, valid customer type, etc.
    }
    
    public boolean isInternationalOrder(ShippingAddress address) {
        // Check if country is not "USA"
    }
    
    public int getTotalQuantity(Order order) {
        // Calculate total quantity of all items
    }
    
    public BigDecimal getSubtotal(Order order) {
        // Calculate subtotal (price * quantity for all items)
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
```

## Evaluation Criteria
- Correct implementation of all business rules
- Proper use of BigDecimal for monetary calculations
- Clean, readable code with good naming conventions
- Proper handling of edge cases
- All test cases pass
- Service methods work independently and correctly