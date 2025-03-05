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

    public int[] rotateLeft3(int[] nums) {
        int[] newNums = new int[nums.length + 1];
        for (int i = 0; i < nums.length; i++) {
            newNums[i] = nums[i];
        }
        newNums[nums.length] = nums[0];
        nums = Arrays.stream(newNums).skip(1).toArray();
        return nums;
    }

    public int[] reverse3(int[] nums) {
        int[] newNums = new int[nums.length];
        int j = nums.length - 1;
        for (int i = 0; i < nums.length; i++) {
            newNums[j] = nums[i];
            j--;
        }
        return newNums;
    }

    public int[] maxEnd3(int[] nums) {
        int firstNumsValue = nums[0];
        int lastNumsValue = nums[nums.length - 1];
        if (firstNumsValue >= lastNumsValue) {
            Arrays.fill(nums, firstNumsValue);
        }
        if (lastNumsValue > firstNumsValue) {
            Arrays.fill(nums, lastNumsValue);
        }
        return nums;
    }

    public int sum2(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        if (nums.length == 1) {
            return nums[0];
        }
        return nums[0] + nums[1];
    }

    public int[] middleWay(int[] a, int[] b) {
        int[] middleNums = new int[2];
        middleNums[0] = a[1];
        middleNums[1] = b[1];
        return middleNums;
    }

    public int[] makeEnds(int[] nums) {
        int[] endsNums = new int[2];
        endsNums[0] = nums[0];
        endsNums[1] = nums[nums.length - 1];
        return endsNums;
    }

    public boolean has23(int[] nums) {
        boolean result = false;
        for (int num : nums) {
            if (num == 2 || num == 3) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean no23(int[] nums) {
        boolean result = true;
        for (int num : nums) {
            if (num == 2 || num == 3) {
                result = false;
                break;
            }
        }
        return result;
    }

    public int[] makeLast(int[] nums) {
        int length = nums.length * 2;
        int[] newNums = new int[length];
        newNums[length - 1] = nums[nums.length - 1];
        return newNums;
    }

    public boolean double23(int[] nums) {
        if (nums.length < 2) {
            return false;
        } else {
            return nums[0] == nums[1];
        }
    }

    public int[] fix23(int[] nums) {
        for (int i = 1; i < nums.length; i++) {
            if (nums[i - 1] == 2 && nums[i] == 3) {
                nums[i] = 0;
            }
        }
        return nums;
    }

    public int start1(int[] a, int[] b) {
        int result = 0;
        if (a.length == 0 && b.length == 0) {
            return 0;
        }
        if (a.length == 0) {
            if (b[0] == 1) {
                return 1;
            } else {
                return 0;
            }
        }
        if (b.length == 0) {
            if (a[0] == 1) {
                return 1;
            } else {
                return 0;
            }
        }
        if (a[0] == 1) {
            result++;
        }
        if (b[0] == 1) {
            result++;
        }
        return result;
    }

    public int[] biggerTwo(int[] a, int[] b) {
        if (Arrays.stream(a).sum() >= Arrays.stream(b).sum()) {
            return a;
        } else {
            return b;
        }
    }

    public int[] makeMiddle(int[] nums) {
        int firstMiddleValue = (nums.length / 2) - 1;
        int secondMiddleValue = (nums.length / 2);
        int[] result = new int[2];
        if (nums.length <= 2) {
            return nums;
        } else {
            result[0] = nums[firstMiddleValue];
            result[1] = nums[secondMiddleValue];
            return result;
        }
    }
}
