package tasks.dominoTask;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    public int solution(int[] A) {
        if (A.length > 50000) {
            throw new RuntimeException("a.length is too much");
        }
        if (A.length <= 2) {
            return 0;
        }

        List<Pair> pairList = createPairList(A);
        return findMaxChain(pairList);
    }

    private int findMaxChain(List<Pair> pairs) {
        int maxResult = 0;

        for (int startIdx = 0; startIdx < pairs.size(); startIdx++) {
            List<Pair> availablePairs = new ArrayList<>(pairs);
            Pair currentPair = availablePairs.remove(startIdx);
            int result = 0;

            boolean foundConnection = true;
            while (foundConnection && !availablePairs.isEmpty()) {
                foundConnection = false;

                for (int i = 0; i < availablePairs.size(); i++) {
                    Pair candidatePair = availablePairs.get(i);

                    if (currentPair.second.equals(candidatePair.first)) {
                        currentPair = new Pair(currentPair.first, candidatePair.second);
                        availablePairs.remove(i);
                        result++;
                        foundConnection = true;
                        break;
                    } else if (currentPair.second.equals(candidatePair.second)) {
                        currentPair = new Pair(currentPair.first, candidatePair.first);
                        availablePairs.remove(i);
                        result++;
                        foundConnection = true;
                        break;
                    } else if (currentPair.first.equals(candidatePair.first)) {
                        currentPair = new Pair(candidatePair.second, currentPair.second);
                        availablePairs.remove(i);
                        result++;
                        foundConnection = true;
                        break;
                    } else if (currentPair.first.equals(candidatePair.second)) {
                        currentPair = new Pair(candidatePair.first, currentPair.second);
                        availablePairs.remove(i);
                        result++;
                        foundConnection = true;
                        break;
                    }
                }
            }

            maxResult = Math.max(maxResult, result);
        }

        return maxResult;
    }

    private List<Pair> createPairList(int[] A) {
        List<Pair> pairList = new ArrayList<>();
        for (int i = 0; i < A.length; i += 2) {
            pairList.add(new Pair(A[i], A[i + 1]));
        }
        return pairList;
    }
}