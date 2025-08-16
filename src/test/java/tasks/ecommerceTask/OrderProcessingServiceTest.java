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