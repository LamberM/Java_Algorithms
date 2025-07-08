package codingbat.recursion;

public class Recursion1 {
    public int factorial(int n) {
        if (n <= 1) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

    public int bunnyEars(int bunnies) {
        if (bunnies < 1) {
            return 0;
        } else {
            return bunnies * 2;
        }
    }

    public int fibonacci(int n) {
        if (n == 0){
            return 0;
        }
        if (n==1){
            return 1;
        }else {
            return fibonacci(n-1) + fibonacci(n-2);
        }
    }

}
