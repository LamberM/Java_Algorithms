package AP;

import java.util.HashMap;
import java.util.Map;

public class Map2 {
    public Map<String, Integer> word0(String[] strings) {
        Map<String, Integer> resultMap = new HashMap<>();
        for (String string : strings) {
            resultMap.put(string, 0);
        }
        return resultMap;
    }

    public Map<String, Integer> wordLen(String[] strings) {
        Map<String, Integer> resultMap = new HashMap<>();
        for (String string : strings) {
            resultMap.put(string, string.length());
        }
        return resultMap;
    }

    public Map<String, String> pairs(String[] strings) {
        Map<String, String> resultMap = new HashMap<>();
        for (String string : strings) {
            resultMap.put(string.substring(0, 1), string.substring(string.length() - 1));
        }
        return resultMap;
    }

    public Map<String, Integer> wordCount(String[] strings) {
        Map<String, Integer> resultMap = new HashMap<>();
        for (String string : strings) {
            if (resultMap.containsKey(string)) {
                var previousValue = resultMap.get(string);
                resultMap.replace(string, previousValue + 1);
            } else {
                resultMap.put(string, 1);
            }
        }
        return resultMap;
    }

    public Map<String, String> firstChar(String[] strings) {
        Map<String, String> resultMap = new HashMap<>();
        if (strings.length == 0) {
            return new HashMap<>();
        } else {
            for (String string : strings) {
                if (resultMap.containsKey(string.substring(0, 1))) {
                    var previousValue = resultMap.get(string.substring(0, 1));
                    resultMap.replace(string.substring(0, 1), previousValue + string);
                } else {
                    resultMap.put(string.substring(0, 1), string);
                }
            }
            return resultMap;
        }
    }

}
