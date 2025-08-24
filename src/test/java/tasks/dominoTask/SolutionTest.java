package tasks.dominoTask;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    private final Solution solution = new Solution();

    @Test
    void testBasicChain() {
        int[] input = {2, 4, 1, 3, 4, 6, 2, 4, 1, 6};
        int expected = 4;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    void testComplexChain() {
        int[] input = {5, 1, 2, 6, 6, 1, 3, 1, 4, 3, 4, 3, 4, 6, 1, 2, 4, 1, 6, 2};
        int expected = 7;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    void testShortChain() {
        int[] input = {1, 5, 3, 3, 1, 3};
        int expected = 2;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    void testSinglePair() {
        int[] input = {3, 4};
        int expected = 0;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    void testEmptyArray() {
        int[] input = {};
        int expected = 0;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    void testTwoPairsNoConnection() {
        int[] input = {1, 2, 3, 4};
        int expected = 0;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    void testTwoPairsDirectConnection() {
        int[] input = {1, 2, 2, 3};
        int expected = 1;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    void testTwoPairsFlipConnection() {
        int[] input = {1, 2, 3, 2};
        int expected = 1;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    void testPerfectLoop() {
        int[] input = {1, 2, 2, 3, 3, 1};
        int expected = 2;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    void testAllSameNumbers() {
        int[] input = {5, 5, 5, 5, 5, 5};
        int expected = 2;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    void testDoublesOnly() {
        int[] input = {1, 1, 2, 2, 3, 3};
        int expected = 0;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    void testLongChainMultipleOptions() {
        int[] input = {1, 2, 2, 3, 3, 4, 4, 5, 5, 6};
        int expected = 4;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test 20: Maximum length constraint")
    void testMaximumLengthConstraint() {
        assertThrows(RuntimeException.class, () -> {
            int[] input = new int[50001];
            solution.solution(input);
        });
    }

    @Test
    void testReverseConnectionOrder() {
        int[] input = {6, 5, 5, 4, 4, 3, 3, 2, 2, 1};
        int expected = 4;
        int actual = solution.solution(input);
        assertEquals(expected, actual);
    }

    @Test
    void testPerformanceMediumSize() {
        int[] input = new int[1000];
        for (int i = 0; i < 1000; i += 2) {
            input[i] = i / 2;
            input[i + 1] = (i / 2) + 1;
        }

        long startTime = System.currentTimeMillis();
        int result = solution.solution(input);
        long endTime = System.currentTimeMillis();

        assertTrue(result >= 0);
        assertTrue(endTime - startTime < 1000); // Should complete within 1 second
    }
}