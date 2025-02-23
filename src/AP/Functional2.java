package AP;

import java.util.List;
import java.util.stream.Collectors;

public class Functional2 {
    public List<Integer> noNeg(List<Integer> nums) {
        return nums.stream()
                .filter(integer -> integer >= 0)
                .collect(Collectors.toList());
    }
    public List<Integer> no9(List<Integer> nums) {
        return nums.stream()
                .filter(integer -> integer == integer % 9)
                .collect(Collectors.toList());
    }
    public List<Integer> noTeen(List<Integer> nums) {
        return nums.stream()
                .filter(integer -> integer < 13 || integer > 19)
                .collect(Collectors.toList());
    }
    public List<String> noZ(List<String> strings) {
        return strings.stream()
                .filter(s -> !s.contains("z"))
                .collect(Collectors.toList());
    }

}
