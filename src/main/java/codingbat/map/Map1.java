package codingbat.map;

import java.util.Map;

public class Map1 {
    public Map<String, String> mapBully(Map<String, String> map) {
        if (map.containsKey("a")) {
            map.put("b", map.get("a"));
            map.replace("a", "");
        }
        return map;
    }

    public Map<String, String> mapShare(Map<String, String> map) {
        if (map.containsKey("a")) {
            map.put("b", map.get("a"));
        }
        map.remove("c");
        return map;
    }

    public Map<String, String> mapAB(Map<String, String> map) {
        if (map.containsKey("a") && map.containsKey("b")) {
            map.put("ab", map.get("a") + map.get("b"));
        }
        return map;
    }

    public Map<String, String> topping1(Map<String, String> map) {
        if (map.containsKey("ice cream")) {
            map.replace("ice cream", "cherry");
        }
        map.put("bread", "butter");
        return map;
    }

    public Map<String, String> topping2(Map<String, String> map) {
        if (map.containsKey("ice cream")) {
            map.put("yogurt", map.get("ice cream"));
        }
        map.replace("spinach", "nuts");
        return map;
    }

    public Map<String, String> topping3(Map<String, String> map) {
        if (map.containsKey("potato")) {
            map.put("fries", map.get("potato"));
        }
        if (map.containsKey("salad")) {
            map.put("spinach", map.get("salad"));
        }
        return map;
    }

    public Map<String, String> mapAB2(Map<String, String> map) {
        if (map.containsKey("a") && map.containsKey("b")) {
            if (map.get("a").equals(map.get("b"))) {
                map.remove("a");
                map.remove("b");
            }
        }
        return map;
    }

    public Map<String, String> mapAB3(Map<String, String> map) {
        if (map.containsKey("a") && !map.containsKey("b")) {
            map.put("b", map.get("a"));
        }
        if (map.containsKey("b") && !map.containsKey("a")) {
            map.put("a", map.get("b"));
        }
        return map;
    }

    public Map<String, String> mapAB4(Map<String, String> map) {
        if (map.containsKey("c")) {
            if (map.containsKey("a") && map.containsKey("b")) {
                if (map.get("a").length()==map.get("b").length()){
                    map.replace("a","");
                    map.replace("b","");
                }
                if (map.get("a").length()>map.get("b").length()){
                    map.replace("c",map.get("a"));
                }
                if (map.get("a").length()<map.get("b").length()){
                    map.replace("c",map.get("b"));
                }
            }
        }
        else {
            if (map.containsKey("a") && map.containsKey("b")) {
                if (map.get("a").length()==map.get("b").length()){
                    map.replace("a","");
                    map.replace("b","");
                }
                if (map.get("a").length()>map.get("b").length()){
                    map.put("c",map.get("a"));
                }
                if (map.get("a").length()<map.get("b").length()){
                    map.put("c",map.get("b"));
                }
            }
        }
        return map;
    }

}
