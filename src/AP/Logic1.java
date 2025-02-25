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
        if (day > 0 && day <= 5 && !vacation) return "7:00";
        if (day > 0 && day <= 5) return "10:00";
        if ((day == 0 || day == 6) && !vacation) return "10:00";
        else return "off";
    }

    public boolean love6(int a, int b) {
        int sum = a + b;
        int diff = Math.abs(a - b);
        return a == 6 || b == 6 || sum == 6 || diff == 6;
    }

}
