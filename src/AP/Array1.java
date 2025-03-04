package AP;

import java.util.Arrays;

public class Array1 {
    public boolean firstLast6(int[] nums) {
        return nums[0] == 6 || nums[nums.length - 1] == 6;
    }

    public boolean sameFirstLast(int[] nums) {
        if (nums.length == 0) {
            return false;
        }
        return nums[0] == nums[nums.length - 1];
    }

    public int[] makePi() {
        int[] nums = new int[3];
        nums[0] = 3;
        nums[1] = 1;
        nums[2] = 4;
        return nums;
    }

    public boolean commonEnd(int[] a, int[] b) {
        return a[0] == b[0] || a[a.length - 1] == b[b.length - 1];
    }

    public int sum3(int[] nums) {
        return Arrays.stream(nums).sum();
    }

}
