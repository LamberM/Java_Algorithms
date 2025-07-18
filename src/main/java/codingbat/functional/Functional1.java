package codingbat.functional;

import java.util.List;

public class Functional1 {
    public List<Integer> doubling(List<Integer> nums) {
        nums.replaceAll(num -> num * 2);
        return nums;
    }

    public List<Integer> square(List<Integer> nums) {
        nums.replaceAll(num -> num * num);
        return nums;
    }

    public List<String> addStar(List<String> strings) {
        strings.replaceAll(s -> s + "*");
        return strings;
    }

    public List<String> copies3(List<String> strings) {
        strings.replaceAll(s -> s + s + s);
        return strings;
    }
    public List<String> moreY(List<String> strings) {
        strings.replaceAll(s-> "y"+s+"y");
        return strings;
    }
    public List<Integer> math1(List<Integer> nums) {
        nums.replaceAll(num -> (num+1) * 10);
        return nums;
    }
    public List<Integer> rightDigit(List<Integer> nums) {
        nums.replaceAll(num -> num%10);
        return nums;
    }
    public List<String> lower(List<String> strings) {
        strings.replaceAll(String::toLowerCase);
        return strings;
    }
    public List<String> noX(List<String> strings) {
        strings.replaceAll(s -> s.replace("x",""));
        return strings;
    }


}
