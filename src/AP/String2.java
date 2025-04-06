package AP;

public class String2 {
    public String doubleChar(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            result.append(str.charAt(i)).append(str.charAt(i));
        }
        return result.toString();
    }

    public int countHi(String str) {
        int result = 0;
        if (str.length() >= 2) {
            for (int i = 1; i < str.length(); i++) {
                if (str.charAt(i - 1) == 'h' && str.charAt(i) == 'i') {
                    result = result + 1;
                }
            }
            return result;
        } else {
            return 0;
        }
    }

    public boolean catDog(String str) {
        int catResult = 0;
        int dogResult = 0;
        if (str.length() >= 3) {
            for (int i = 2; i < str.length(); i++) {
                if (str.charAt(i - 2) == 'c' && str.charAt(i - 1) == 'a' && str.charAt(i) == 't') {
                    catResult = catResult + 1;
                }
                if (str.charAt(i - 2) == 'd' && str.charAt(i - 1) == 'o' && str.charAt(i) == 'g') {
                    dogResult = dogResult + 1;
                }
            }
            return dogResult == catResult;
        } else {
            return true;
        }
    }

    public int countCode(String str) {
        int result = 0;
        if (str.length() >= 4) {
            for (int i = 3; i < str.length(); i++) {
                if (str.charAt(i - 3) == 'c' && str.charAt(i - 2) == 'o' && str.charAt(i) == 'e') {
                    result = result + 1;
                }
            }
            return result;
        } else return 0;
    }

    public boolean endOther(String a, String b) {
        if (a.length() > b.length()) {
            return a.toLowerCase().substring(a.length() - b.length(), a.length()).equals(b.toLowerCase());
        }
        if (a.length() < b.length()) {
            return b.toLowerCase().substring(b.length() - a.length(), b.length()).equals(a.toLowerCase());
        }
        return a.equalsIgnoreCase(b);
    }

    public boolean xyzThere(String str) {
        boolean result = false;
        if (str.length() == 3) {
            return str.contains("xyz");
        }
        if (str.length() > 3) {
            for (int i = 3; i < str.length(); i++) {
                result = str.startsWith("xyz");
                if (str.charAt(i - 3) != '.' && str.charAt(i - 2) == 'x' && str.charAt(i - 1) == 'y' && str.charAt(i) == 'z') {
                    result = true;
                }
                if (str.charAt(i - 3) == '.' && str.charAt(i - 2) == 'x' && str.charAt(i - 1) == 'y' && str.charAt(i) == 'z') {
                    result = false;
                }
            }
            return result;
        } else {
            return false;
        }
    }
}
