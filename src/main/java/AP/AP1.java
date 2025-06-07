package AP;

public class AP1 {
    public boolean scoresIncreasing(int[] scores) {
        int lastScore = 0;
        boolean result = false;
        for (int i=1; i < scores.length; i++) {
            if(lastScore > scores[i]){
                result = false;
                break;
            }
            else{
                result = true;
                lastScore = scores[i];
            }
        }
        return result;
    }
    public boolean scores100(int[] scores) {
        int lastScore = 0;
        boolean result = false;
        for (int i=0; i < scores.length; i++) {
            if(lastScore == scores[i]){
                result = true;
                break;
            }
            else{
                lastScore = scores[i];
            }
        }
        return result;
    }

}
