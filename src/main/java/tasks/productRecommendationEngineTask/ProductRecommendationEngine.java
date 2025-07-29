package tasks.productRecommendationEngineTask;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ProductRecommendationEngine {

    private final List<Product> products;
    private final List<Customer> customers;
    private final List<Purchase> purchases;
    private final RecommendationConfig config;

    // Thread-safe caches
    private final Map<String, List<Recommendation>> recommendationCache;
    private final Map<String, LocalDateTime> cacheTimestamps;

    public ProductRecommendationEngine(List<Product> products,
                                       List<Customer> customers,
                                       List<Purchase> purchases,
                                       RecommendationConfig config) {
        this.products = products;
        this.customers = customers;
        this.purchases = new ArrayList<>(purchases);
        this.config = config;
        this.recommendationCache = new ConcurrentHashMap<>();
        this.cacheTimestamps = new ConcurrentHashMap<>();
    }

    // ==================== METHODS TO IMPLEMENT ====================

    /**
     * Generate personalized product recommendations for a customer
     *
     * @param customerId The customer ID to generate recommendations for
     * @return List of recommendations sorted by score (highest first)
     */
    public List<Recommendation> generateRecommendations(String customerId) {
        List<Recommendation> recommendations;
        Map<String, Double> getPopularProducts;
        if (isCacheValid(customerId, 60)) {
            return recommendationCache.get(customerId);
        }
        if (customerId.isEmpty()) {
            throw new IllegalArgumentException("customer id should not empty");
        }
        if (!isExistCustomerId(customerId)) {
            Set<String> getNotActiveProductSet = products.stream().filter(product -> !product.active()).map(Product::productId).collect(Collectors.toSet());
            getPopularProducts = getPopularProducts(getNotActiveProductSet, config.maxRecommendations());
            recommendations = getPopularProducts.entrySet().stream().map(entry -> new Recommendation(entry.getKey(), entry.getValue(), "popular", LocalDateTime.now())).toList();
            return recommendations;
        } else {
            Set<String> getProductIdSetFromCustomerId = purchases.stream().filter(purchase -> purchase.customerId().equals(customerId)).map(Purchase::productId).collect(Collectors.toSet());
            getPopularProducts = getPopularProducts(getProductIdSetFromCustomerId, config.maxRecommendations());
            recommendations = getPopularProducts.entrySet().stream().map(entry -> new Recommendation(entry.getKey(), entry.getValue(), "popular", LocalDateTime.now())).toList();
            recommendationCache.put(customerId, recommendations);
            cacheTimestamps.put(customerId, LocalDateTime.now());
            return recommendations;
        }
    }

    /**
     * Find customers similar to the given customer based on purchase patterns
     *
     * @param customerId    The target customer ID
     * @param minSimilarity Minimum similarity score (0.0 to 1.0)
     * @return Map of similar customer IDs to their similarity scores
     */
    public Map<String, Double> findSimilarCustomers(String customerId, double minSimilarity) {
        Map<String, Double> result = new HashMap<>();
        Set<String> productIdListFromCustomerId = getCustomerPurchasedProducts(customerId);
        List<Purchase> purchaseListFilteredByProductId = purchases.stream()
                .filter(purchase -> productIdListFromCustomerId.contains(purchase.productId()) && !purchase.customerId().equals(customerId))
                .toList();
        double similarity;
        for (Purchase purchase : purchaseListFilteredByProductId) {
            var customerIdFromPurchase = purchase.customerId();
            var amountFromPurchases = getAmountFromPurchaseList(customerIdFromPurchase, purchases);
            var amountFromPurchaseListFilteredByProductId = getAmountFromPurchaseList(customerIdFromPurchase, purchaseListFilteredByProductId);
            similarity = amountFromPurchaseListFilteredByProductId / amountFromPurchases;
            if (similarity >= minSimilarity) {
                result.put(customerIdFromPurchase, similarity);
            }
        }
        return result;
    }

    private double getAmountFromPurchaseList(String customerId, List<Purchase> purchaseList) {
        return purchaseList.stream().filter(purchase -> purchase.customerId().equals(customerId)).count();
    }

    /**
     * Calculate collaborative filtering recommendations
     *
     * @param customerId       The customer ID
     * @param similarCustomers Map of similar customers and their similarity scores
     * @return Map of product IDs to their recommendation scores
     */
    public Map<String, Double> calculateCollaborativeRecommendations(String customerId,
                                                                     Map<String, Double> similarCustomers) {
        Map<String, Double> result = new HashMap<>();
        Set<String> productIdListFromCustomerId = getCustomerPurchasedProducts(customerId);
        List<Purchase> filteredPurchases = purchases.stream()
                .filter(purchase -> !productIdListFromCustomerId.contains(purchase.productId()))
                .toList();
        var collaborativeScore = 0.0;
        for (Map.Entry<String, Double> similarCustomer : similarCustomers.entrySet()) {
            var customerIdFromSet = similarCustomer.getKey();
            var similarity = similarCustomer.getValue();

            for (Purchase purchase : filteredPurchases) {
                var customerIdFromPurchase = purchase.customerId();
                var productId = purchase.productId();
                if (customerIdFromSet.equals(customerIdFromPurchase)) {
                    var purchaseFrequency = calculatePurchaseFrequency(customerIdFromSet, productId);
                    collaborativeScore += similarity * purchaseFrequency;
                    result.put(productId, collaborativeScore);
                }
            }
            collaborativeScore = 0.0;
        }
        return result;
    }

    /**
     * Get popular products for popularity-based recommendations
     *
     * @param excludeProductIds Products to exclude from results
     * @param limit             Maximum number of products to return
     * @return Map of product IDs to their popularity scores
     */
    public Map<String, Double> getPopularProducts(Set<String> excludeProductIds, int limit) {
        List<String> mostPopularProductList = purchases.stream().map(Purchase::productId).filter(productId -> !excludeProductIds.contains(productId)).distinct().toList();
        Map<String, Double> getPopularProducts = new LinkedHashMap<>();
        for (String productId : mostPopularProductList) {
            getPopularProducts.put(productId, getPopularityScore(productId));
        }
        return getPopularProducts.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(limit).collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * Calculate recency weight for a purchase
     *
     * @param purchaseDate  The date of purchase
     * @param referenceDate The reference date (usually current date)
     * @return Recency weight (1.0 for today, decreasing over time)
     */
    public double calculateRecencyWeight(LocalDateTime purchaseDate, LocalDateTime referenceDate) {
        var daysDiff = ChronoUnit.DAYS.between(purchaseDate, referenceDate);
        var decayFactor = 0.1;
        return Math.exp(-daysDiff * decayFactor);
    }

    /**
     * Get customer's purchase history
     *
     * @param customerId The customer ID
     * @return List of purchases for the customer, sorted by date (newest first)
     */
    public List<Purchase> getCustomerPurchases(String customerId) {
        return purchases.stream().filter(purchase -> purchase.customerId().equals(customerId)).sorted((purchase1, purchase2) -> purchase2.purchaseDate().compareTo(purchase1.purchaseDate())).toList();
    }

    /**
     * Get products purchased by a customer
     *
     * @param customerId The customer ID
     * @return Set of product IDs purchased by the customer
     */
    public Set<String> getCustomerPurchasedProducts(String customerId) {
        return purchases.stream().filter(purchase -> purchase.customerId().equals(customerId)).map(Purchase::productId).collect(Collectors.toSet());
    }

    /**
     * Check if recommendations cache is valid for a customer
     *
     * @param customerId    The customer ID
     * @param maxAgeMinutes Maximum age of cache in minutes
     * @return true if cache is valid, false otherwise
     */
    public boolean isCacheValid(String customerId, int maxAgeMinutes) {
        if (customerId.isEmpty()) {
            throw new IllegalArgumentException("customer id should not empty");
        }
        if (!cacheTimestamps.containsKey(customerId)) {
            return false;
        }
        LocalDateTime cacheTime = cacheTimestamps.get(customerId);
        long minutesDiff = ChronoUnit.MINUTES.between(cacheTime, LocalDateTime.now());
        return minutesDiff <= maxAgeMinutes;
    }

    /**
     * Clear expired cache entries
     *
     * @param maxAgeMinutes Maximum age of cache entries in minutes
     */
    public void clearExpiredCache(int maxAgeMinutes) {
        LocalDateTime now = LocalDateTime.now();
        List<String> expiredKeys = new ArrayList<>();

        for (Map.Entry<String, LocalDateTime> entry : cacheTimestamps.entrySet()) {
            long minutesDiff = ChronoUnit.MINUTES.between(entry.getValue(), now);
            if (minutesDiff > maxAgeMinutes) {
                expiredKeys.add(entry.getKey());
            }
        }

        for (String key : expiredKeys) {
            recommendationCache.remove(key);
            cacheTimestamps.remove(key);
        }
    }

    // ==================== HELPER METHODS (OPTIONAL TO IMPLEMENT) ====================

    /**
     * Calculate purchase frequency for a customer-product pair
     */
    private int calculatePurchaseFrequency(String customerId, String productId) {
        return purchases.stream()
                .filter(purchase ->
                        purchase.customerId().equals(customerId) &&
                                purchase.productId().equals(productId))
                .mapToInt(Purchase::quantity)
                .sum();
    }

    private double getPopularityScore(String productId) {
        // For popular products:
        var totalPurchases = getPurchaseCountForProduct(productId);
        var totalCustomers = customers.size();
        var popularityScore = (double) totalPurchases / totalCustomers;

        // Apply recency boost for trending products
        var recencyBoost = config.recencyWeight();
        popularityScore *= (1 + recencyBoost);
        return popularityScore * (1 + recencyBoost);
    }

    private int getPurchaseCountForProduct(String productId) {
        return purchases.stream().filter(purchase -> purchase.productId().equals(productId)).toList().size();
    }

    private boolean isExistCustomerId(String customerId) {
        return customers.stream().anyMatch(customer -> customer.customerId().equals(customerId));
    }
}
