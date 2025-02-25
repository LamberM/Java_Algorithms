package AP;

public class Logic1 {
    public boolean cigarParty(int cigars, boolean isWeekend) {
        return cigars >= 40 && (cigars <= 60 || isWeekend);
    }

    public int dateFashion(int you, int date) {
        if ((you > 8 && date > 2) || (date > 8 && you > 2)) {
            return 2;
        }
        if (you <= 2 || date <= 2) {
            return 0;
        }
        return 1;
    }

    public boolean squirrelPlay(int temp, boolean isSummer) {
        return (temp >= 60 && temp <= 90) || (temp >= 60 && temp <= 100 && isSummer);
    }

    public int caughtSpeeding(int speed, boolean isBirthday) {
        if (isBirthday) speed = speed - 5;
        if (speed <= 60) return 0;
        if (speed <= 80) return 1;
        return 2;
    }

    public int sortaSum(int a, int b) {
        int sum = a + b;
        if (sum >= 10 && sum <= 19) return 20;
        else return sum;
    }

    public String alarmClock(int day, boolean vacation) {
        if (vacation){
            if (day > 0 && day <= 5) return "10:00";
            else return "off";
        }
        else {
            if (day > 0 && day <= 5) return "7:00";
            else return "10:00";
        }
    }

    public boolean love6(int a, int b) {
        int sum = a + b;
        int diff = Math.abs(a - b);
        return a == 6 || b == 6 || sum == 6 || diff == 6;
    }

    public boolean in1To10(int n, boolean outsideMode) {
        if(outsideMode){
            return n <= 1 || n >= 10;
        }else {
            return n > 0 && n <= 10;
        }
    }
    public boolean specialEleven(int n) {
        return n%11==0 || n%11==1;
    }
    public boolean more20(int n) {
        return n%20==1 || n%20==2;
    }
    public boolean old35(int n) {
        if (n%3==0&&n%5==0)return false;
        return n%3==0||n%5==0;
    }
}
