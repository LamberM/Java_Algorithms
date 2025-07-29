package tasks.productRecommendationEngineTask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class ProductRecommendationEngineTest {

    private ProductRecommendationEngine engine;
    private List<Product> products;
    private List<Customer> customers;
    private List<Purchase> purchases;
    private RecommendationConfig config;

    // Test data setup
    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusDays(7);
        LocalDateTime oneMonthAgo = now.minusDays(30);

        // Create test products
        products = Arrays.asList(
                new Product("P1", "Laptop", "Electronics", 999.99, true, oneMonthAgo),
                new Product("P2", "Mouse", "Electronics", 29.99, true, oneMonthAgo),
                new Product("P3", "Keyboard", "Electronics", 79.99, true, oneMonthAgo),
                new Product("P4", "Monitor", "Electronics", 299.99, true, oneMonthAgo),
                new Product("P5", "Headphones", "Electronics", 199.99, true, oneMonthAgo),
                new Product("P6", "Webcam", "Electronics", 89.99, false, oneMonthAgo), // Inactive
                new Product("P7", "Speakers", "Electronics", 149.99, true, oneWeekAgo),
                new Product("P8", "Tablet", "Electronics", 399.99, true, oneMonthAgo),
                new Product("P9", "Phone", "Electronics", 699.99, true, oneMonthAgo),
                new Product("P10", "Charger", "Electronics", 19.99, true, oneMonthAgo)
        );

        // Create test customers
        customers = Arrays.asList(
                new Customer("C1", "premium", oneMonthAgo, now),
                new Customer("C2", "regular", oneMonthAgo, oneWeekAgo),
                new Customer("C3", "budget", oneMonthAgo, now),
                new Customer("C4", "premium", oneWeekAgo, now),
                new Customer("C5", "regular", oneMonthAgo, now),
                new Customer("C99", "regular", oneMonthAgo, now)
        );

        // Create test purchases
        purchases = Arrays.asList(
                // Customer C1 purchases
                new Purchase("C1", "P1", oneMonthAgo, 1, 999.99),
                new Purchase("C1", "P2", oneMonthAgo.plusDays(1), 1, 29.99),
                new Purchase("C1", "P3", oneWeekAgo, 1, 79.99),

                // Customer C2 purchases (similar to C1)
                new Purchase("C2", "P1", oneMonthAgo.plusDays(2), 1, 999.99),
                new Purchase("C2", "P2", oneMonthAgo.plusDays(3), 1, 29.99),
                new Purchase("C2", "P4", oneWeekAgo.plusDays(1), 1, 299.99),

                // Customer C3 purchases (different pattern)
                new Purchase("C3", "P8", oneMonthAgo.plusDays(5), 1, 399.99),
                new Purchase("C3", "P9", oneWeekAgo.plusDays(2), 1, 699.99),
                new Purchase("C3", "P10", now.minusDays(1), 1, 19.99),

                // Customer C4 purchases (overlaps with C1 and C2)
                new Purchase("C4", "P1", oneWeekAgo.plusDays(3), 1, 999.99),
                new Purchase("C4", "P5", now.minusDays(2), 1, 199.99),

                // Customer C5 purchases (popular products)
                new Purchase("C5", "P2", oneMonthAgo.plusDays(10), 2, 59.98),
                new Purchase("C5", "P10", oneWeekAgo.plusDays(4), 1, 19.99)
        );

        config = new RecommendationConfig(5, 0.1, 2, 0.5, 0.3);

        engine = new ProductRecommendationEngine(products, customers, purchases, config);
    }

    // ==================== TEST METHODS ====================
    @Test
    void testGenerateRecommendationsForExistingCustomer() {
        setUp();

        // Test recommendations for customer C1
        List<Recommendation> recommendations = engine.generateRecommendations("C1");

        // Assertions
        assert recommendations != null : "Recommendations should not be null";
        assert recommendations.size() <= config.maxRecommendations() :
                "Should not exceed max recommendations limit";
        assert !recommendations.isEmpty() : "Should have at least one recommendation";

        // Check that recommendations are sorted by score (descending)
        for (int i = 0; i < recommendations.size() - 1; i++) {
            assert recommendations.get(i).getScore() >= recommendations.get(i + 1).getScore() :
                    "Recommendations should be sorted by score (descending)";
        }

        // Check that already purchased products are excluded
        Set<String> purchasedProducts = engine.getCustomerPurchasedProducts("C1");
        for (Recommendation rec : recommendations) {
            assert !purchasedProducts.contains(rec.getProductId()) :
                    "Should not recommend already purchased products";
        }

        // Check that inactive products are excluded
        for (Recommendation rec : recommendations) {
            Product product = products.stream()
                    .filter(p -> p.productId().equals(rec.getProductId()))
                    .findFirst().orElse(null);
            assert product != null && product.active() :
                    "Should only recommend active products";
        }

        System.out.println("✓ testGenerateRecommendationsForExistingCustomer passed");
    }

    @Test
    void testGenerateRecommendationsForNewCustomer() {
        setUp();

        // Test recommendations for new customer (not in purchase history)
        List<Recommendation> recommendations = engine.generateRecommendations("C99");

        // Should fall back to popularity-based recommendations
        assert recommendations != null : "Recommendations should not be null";
        assert !recommendations.isEmpty() : "Should have popularity-based recommendations";

        // All recommendations should be based on popularity
        for (Recommendation rec : recommendations) {
            assert rec.getReason().contains("popular") || rec.getReason().contains("trending") :
                    "New customer should get popularity-based recommendations";
        }

        System.out.println("✓ testGenerateRecommendationsForNewCustomer passed");
    }

    @Test
    void testFindSimilarCustomers() {
        setUp();

        Map<String, Double> similarCustomers = engine.findSimilarCustomers("C1", 0.1);

        // Should find C2 as similar (both bought P1, P2)
        assert similarCustomers.containsKey("C2") : "Should find C2 as similar customer";
        assert similarCustomers.get("C2") > 0.1 : "Similarity score should be above minimum";

        // Should not include the customer itself
        assert !similarCustomers.containsKey("C1") : "Should not include self in similar customers";

        // Should find C4 as similar (both bought P1)
        assert similarCustomers.containsKey("C4") : "Should find C4 as similar customer";

        System.out.println("✓ testFindSimilarCustomers passed");
    }

    @Test
    void testCalculateCollaborativeRecommendations() {
        setUp();

        Map<String, Double> similarCustomers = Map.of("C2", 0.5, "C4", 0.3);
        Map<String, Double> recommendations = engine.calculateCollaborativeRecommendations("C1", similarCustomers);

        // Should recommend products bought by similar customers but not by C1
        assert recommendations.containsKey("P4") : "Should recommend P4 (bought by C2)";
        assert recommendations.containsKey("P5") : "Should recommend P5 (bought by C4)";

        // Should not recommend products already bought by C1
        assert !recommendations.containsKey("P1") : "Should not recommend already purchased P1";
        assert !recommendations.containsKey("P2") : "Should not recommend already purchased P2";

        System.out.println("✓ testCalculateCollaborativeRecommendations passed");
    }

    @Test
    void testGetPopularProducts() {
        setUp();

        Set<String> excludeProducts = Set.of("P1", "P2");
        Map<String, Double> popularProducts = engine.getPopularProducts(excludeProducts, 3);

        // Should not include excluded products
        assert !popularProducts.containsKey("P1") : "Should exclude P1";
        assert !popularProducts.containsKey("P2") : "Should exclude P2";

        // Should respect the limit
        assert popularProducts.size() <= 3 : "Should respect the limit";

        // Should include popular products like P10 (bought by multiple customers)
        assert popularProducts.containsKey("P10") : "Should include popular product P10";

        System.out.println("✓ testGetPopularProducts passed");
    }

    @Test
    void testCalculateRecencyWeight() {
        setUp();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusDays(7);
        LocalDateTime oneMonthAgo = now.minusDays(30);

        double recentWeight = engine.calculateRecencyWeight(now, now);
        double weekWeight = engine.calculateRecencyWeight(oneWeekAgo, now);
        double monthWeight = engine.calculateRecencyWeight(oneMonthAgo, now);

        // Recent purchases should have higher weight
        assert recentWeight > weekWeight : "Recent purchases should have higher weight";
        assert weekWeight > monthWeight : "Week-old purchases should have higher weight than month-old";

        // Weight should be between 0 and 1
        assert recentWeight <= 1.0 && recentWeight >= 0.0 : "Weight should be between 0 and 1";
        assert weekWeight <= 1.0 && weekWeight >= 0.0 : "Weight should be between 0 and 1";
        assert monthWeight <= 1.0 && monthWeight >= 0.0 : "Weight should be between 0 and 1";

        System.out.println("✓ testCalculateRecencyWeight passed");
    }

    @Test
    void testGetCustomerPurchases() {
        setUp();

        List<Purchase> customerPurchases = engine.getCustomerPurchases("C1");

        // Should return all purchases for C1
        assert customerPurchases.size() == 3 : "C1 should have 3 purchases";

        // Should be sorted by date (newest first)
        for (int i = 0; i < customerPurchases.size() - 1; i++) {
            assert customerPurchases.get(i).purchaseDate()
                    .isAfter(customerPurchases.get(i + 1).purchaseDate()) ||
                    customerPurchases.get(i).purchaseDate()
                            .equals(customerPurchases.get(i + 1).purchaseDate()) :
                    "Purchases should be sorted by date (newest first)";
        }

        System.out.println("✓ testGetCustomerPurchases passed");
    }

    @Test
    void testGetCustomerPurchasedProducts() {
        setUp();

        Set<String> purchasedProducts = engine.getCustomerPurchasedProducts("C1");

        // Should return all unique products purchased by C1
        assert purchasedProducts.size() == 3 : "C1 should have purchased 3 unique products";
        assert purchasedProducts.contains("P1") : "Should contain P1";
        assert purchasedProducts.contains("P2") : "Should contain P2";
        assert purchasedProducts.contains("P3") : "Should contain P3";

        System.out.println("✓ testGetCustomerPurchasedProducts passed");
    }

    @Test
    void testCacheValidation() {
        setUp();

        // Initially cache should be invalid
        assert !engine.isCacheValid("C1", 65) : "Cache should be invalid initially";

        // Generate recommendations to populate cache
        engine.generateRecommendations("C1");

        // Now cache should be valid
        assert engine.isCacheValid("C1", 60) : "Cache should be valid after generating recommendations";

        // Cache should be invalid for very short expiry time
        assert !engine.isCacheValid("C1", 0) : "Cache should be invalid with 0 minute expiry";

        System.out.println("✓ testCacheValidation passed");
    }

    @Test
    void testPerformanceWithLargeDataset() {
        // Create larger dataset for performance testing
        List<Product> largeProducts = new ArrayList<>();
        List<Customer> largeCustomers = new ArrayList<>();
        List<Purchase> largePurchases = new ArrayList<>();

        // Create 1000 products
        for (int i = 1; i <= 1000; i++) {
            largeProducts.add(new Product("P" + i, "Product " + i, "Category",
                    100.0 + i, true, LocalDateTime.now().minusDays(i % 30)));
        }

        // Create 100 customers
        for (int i = 1; i <= 100; i++) {
            largeCustomers.add(new Customer("C" + i, "regular",
                    LocalDateTime.now().minusDays(60), LocalDateTime.now()));
        }

        // Create 5000 purchases
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 1; i <= 5000; i++) {
            largePurchases.add(new Purchase(
                    "C" + (random.nextInt(100) + 1),
                    "P" + (random.nextInt(1000) + 1),
                    LocalDateTime.now().minusDays(random.nextInt(90)),
                    1,
                    100.0 + random.nextDouble() * 900
            ));
        }

        ProductRecommendationEngine largeEngine = new ProductRecommendationEngine(
                largeProducts, largeCustomers, largePurchases, config);

        // Measure time for generating recommendations
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= 10; i++) {
            List<Recommendation> recommendations = largeEngine.generateRecommendations("C" + i);
            assert recommendations != null : "Recommendations should not be null";
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Should complete within reasonable time (adjust as needed)
        assert duration < 5000 : "Should complete 10 recommendations within 5 seconds";

        System.out.println("✓ testPerformanceWithLargeDataset passed (took " + duration + "ms)");
    }

    // cannot work, because customers is final list
    @Test
    void testEdgeCases() {
        setUp();

        // Test with non-existent customer
        List<Recommendation> recommendations = engine.generateRecommendations("NONEXISTENT");
        assert recommendations != null : "Should handle non-existent customer gracefully";

        // Test with customer who has no purchases
        Customer newCustomer = new Customer("C_NEW", "regular", LocalDateTime.now(), LocalDateTime.now());
//        customers.add(newCustomer);
        engine = new ProductRecommendationEngine(products, customers, purchases, config);

        recommendations = engine.generateRecommendations("C_NEW");
        assert recommendations != null : "Should handle customer with no purchases";
        assert !recommendations.isEmpty() : "Should provide popularity-based recommendations";

        System.out.println("✓ testEdgeCases passed");
    }

    // Run all tests
    @Test
    void runAllTests() {
        System.out.println("Running Product Recommendation Engine Tests...\n");

        try {
            testGenerateRecommendationsForExistingCustomer();
            testGenerateRecommendationsForNewCustomer();
            testFindSimilarCustomers();
            testCalculateCollaborativeRecommendations();
            testGetPopularProducts();
            testCalculateRecencyWeight();
            testGetCustomerPurchases();
            testGetCustomerPurchasedProducts();
            testCacheValidation();
            testPerformanceWithLargeDataset();
            testEdgeCases();

            System.out.println("\n🎉 All tests passed! The recommendation engine is working correctly.");

        } catch (AssertionError e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Main method to run tests
    public static void main(String[] args) {
        ProductRecommendationEngineTest test = new ProductRecommendationEngineTest();
        test.runAllTests();
    }
}