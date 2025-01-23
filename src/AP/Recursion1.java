package AP;

import static java.lang.Integer.sum;

public class Recursion1 {
    public int factorial(int n) {
        if (n <= 1) {
            return 1;
        }
        else {
            return n * factorial(n- 1);
        }
    }
}
